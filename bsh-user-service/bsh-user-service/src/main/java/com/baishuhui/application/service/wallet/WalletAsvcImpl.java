package com.baishuhui.application.service.wallet;

import com.baishuhui.user.constant.WalletChannels;
import com.baishuhui.user.constant.WalletConstants;
import com.baishuhui.user.vo.wallet.WalletCreditCommand;
import com.baishuhui.user.vo.wallet.WalletDeductCommand;
import com.baishuhui.user.vo.wallet.WalletRefundCommand;
import com.baishuhui.user.vo.wallet.PaymentResultDTO;
import com.baishuhui.user.vo.wallet.WalletChannelDTO;
import com.baishuhui.user.vo.wallet.WalletDTO;
import com.baishuhui.user.vo.wallet.WalletLedgerDTO;
import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.util.IdUtil;
import com.baishuhui.common.util.MoneyUtil;
import com.baishuhui.interfaces.config.WalletProperties;
import com.baishuhui.domain.wallet.entity.PaymentEntity;
import com.baishuhui.domain.wallet.entity.WalletChannelEntity;
import com.baishuhui.domain.wallet.entity.WalletLedgerEntity;
import com.baishuhui.domain.wallet.repositories.IPaymentRepository;
import com.baishuhui.domain.wallet.repositories.IWalletChannelRepository;
import com.baishuhui.domain.wallet.repositories.IWalletLedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户钱包用例编排：渠道初始化、测试赠送、扣款与退款。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletAsvcImpl implements IWalletAsvc {

    /** 余额并发更新重试次数 */
    private static final int OPTIMISTIC_RETRY = 3;

    /** 流水默认返回条数 */
    private static final int DEFAULT_LEDGER_LIMIT = 20;

    /** 流水最大返回条数，避免深查询 */
    private static final int MAX_LEDGER_LIMIT = 100;

    private final IWalletChannelRepository walletChannelRepository;

    private final IWalletLedgerRepository walletLedgerRepository;

    private final IPaymentRepository paymentRepository;

    private final WalletProperties walletProperties;

    /**
     * 初始化用户五渠道余额行，缺失的补 0.00。
     *
     * @param userId 用户 id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void ensureChannels(String userId) {
        requireUserId(userId);
        // 遍历五渠道，缺失则补零余额行
        for (String channel : WalletChannels.ALL_CHANNELS) {
            // 已存在则跳过，避免重复插入
            if (walletChannelRepository.findByUserAndChannel(userId, channel) != null) {
                continue;
            }
            WalletChannelEntity entity = new WalletChannelEntity();
            entity.setId(IdUtil.nextId());
            entity.setUserId(userId);
            entity.setChannel(channel);
            entity.setBalance(MoneyUtil.scale(BigDecimal.ZERO));
            entity.setVersion(0);
            walletChannelRepository.insert(entity);
        }
    }

    /**
     * 注册测试赠送：同一用户仅一次，仅入账配置渠道（默认系统金额）。
     *
     * @param userId 用户 id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void grantTestFunds(String userId) {
        // 测试赠送开关关闭则直接返回
        if (!walletProperties.isTestGrantEnabled()) {
            return;
        }
        BigDecimal amount = MoneyUtil.scale(walletProperties.getTestGrantAmount());
        // 配置金额无效则不发
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        // 幂等：已有 TEST_GRANT 流水则不再补发
        if (walletLedgerRepository.countByUserAndBizType(userId, WalletConstants.BIZ_TEST_GRANT) > 0) {
            log.info("wallet test grant skipped, already granted userId={}", userId);
            return;
        }
        ensureChannels(userId);
        String channel = WalletChannels.normalize(walletProperties.getTestGrantChannel());
        PaymentEntity payment = newPayment(userId, channel, amount,
                WalletConstants.DIRECTION_GRANT, WalletConstants.BIZ_TEST_GRANT);
        payment.setIdempotentKey(WalletConstants.TEST_GRANT_KEY_PREFIX + userId);
        settle(payment, "注册测试赠送");
        log.info("wallet test grant done userId={} channel={} amount={}", userId, channel, amount);
    }

    /**
     * 测试期自助充值：须开启测试赠送开关；每笔独立幂等键，可多次。
     *
     * @param userId  用户 id
     * @param amount  金额，空则 10000
     * @param channel 渠道，空则系统金额
     * @return 入账结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PaymentResultDTO testTopUp(String userId, BigDecimal amount, String channel) {
        // 测试充值开关关闭则拒绝
        if (!walletProperties.isTestGrantEnabled()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "测试充值已关闭");
        }
        BigDecimal value = amount == null ? new BigDecimal("10000") : MoneyUtil.scale(amount);
        BigDecimal cap = MoneyUtil.scale(walletProperties.getTestGrantAmount());
        // 超过配置上限则截断到上限
        if (cap.compareTo(BigDecimal.ZERO) > 0 && value.compareTo(cap) > 0) {
            value = cap;
        }
        value = requireAmount(value);
        String payChannel = StringUtils.hasText(channel)
                ? WalletChannels.normalize(channel)
                : WalletChannels.normalize(walletProperties.getTestGrantChannel());
        ensureChannels(userId);
        PaymentEntity payment = newPayment(userId, payChannel, value,
                WalletConstants.DIRECTION_GRANT, WalletConstants.BIZ_TEST_TOPUP);
        payment.setIdempotentKey("TOPUP:" + IdUtil.nextId());
        PaymentResultDTO result = settle(payment, "测试充值");
        log.info("wallet test topup userId={} channel={} amount={} balanceAfter={}",
                userId, payChannel, value, result.getBalanceAfter());
        return result;
    }

    /**
     * 管理员调账：向指定用户渠道入账。
     *
     * @param userId  用户 id
     * @param amount  金额
     * @param channel 渠道，空则系统金额
     * @param remark  备注
     * @return 入账结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PaymentResultDTO adjust(String userId, BigDecimal amount, String channel, String remark) {
        BigDecimal value = requireAmount(amount);
        BigDecimal cap = new BigDecimal("1000000");
        // 单次调账上限 100 万
        if (value.compareTo(cap) > 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "单次调账不能超过 1000000");
        }
        String payChannel = StringUtils.hasText(channel)
                ? WalletChannels.normalize(channel)
                : WalletChannels.SYSTEM;
        ensureChannels(userId);
        PaymentEntity payment = newPayment(userId, payChannel, value,
                WalletConstants.DIRECTION_GRANT, WalletConstants.BIZ_ADJUST);
        payment.setIdempotentKey("ADJUST:" + IdUtil.nextId());
        String note = StringUtils.hasText(remark) ? remark.trim() : "管理员调账";
        PaymentResultDTO result = settle(payment, note);
        log.info("wallet adjust userId={} channel={} amount={} balanceAfter={}",
                userId, payChannel, value, result.getBalanceAfter());
        return result;
    }

    /**
     * 钱包总览：各渠道余额与合计，缺失渠道自动补齐。
     *
     * @param userId 用户 id
     * @return 钱包总览
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public WalletDTO getWallet(String userId) {
        ensureChannels(userId);
        Map<String, BigDecimal> balances = new HashMap<>(8);
        // 遍历集合逐项处理
        for (WalletChannelEntity entity : walletChannelRepository.listByUser(userId)) {
            balances.put(entity.getChannel(), MoneyUtil.scale(entity.getBalance()));
        }
        List<WalletChannelDTO> channels = new ArrayList<>(WalletChannels.ALL_CHANNELS.size());
        BigDecimal total = BigDecimal.ZERO;
        // 遍历集合逐项处理
        for (String channel : WalletChannels.ALL_CHANNELS) {
            BigDecimal balance = balances.getOrDefault(channel, MoneyUtil.scale(BigDecimal.ZERO));
            total = total.add(balance);
            channels.add(WalletChannelDTO.builder()
                    .channel(channel)
                    .channelLabel(WalletChannels.label(channel))
                    .balance(balance)
                    .build());
        }
        return WalletDTO.builder()
                .userId(userId)
                .totalBalance(MoneyUtil.scale(total))
                .channels(channels)
                .build();
    }

    /**
     * 用户最近流水。
     *
     * @param userId 用户 id
     * @param limit  条数，非法值走默认
     * @return 流水列表
     */
    @Override
    public List<WalletLedgerDTO> listLedgers(String userId, Integer limit) {
        requireUserId(userId);
        int size = limit == null || limit <= 0 ? DEFAULT_LEDGER_LIMIT : Math.min(limit, MAX_LEDGER_LIMIT);
        List<WalletLedgerEntity> rows = walletLedgerRepository.listRecentByUser(userId, size);
        List<WalletLedgerDTO> list = new ArrayList<>(rows.size());
        // 遍历集合逐项处理
        for (WalletLedgerEntity row : rows) {
            list.add(toLedgerDto(row));
        }
        return list;
    }

    /**
     * 渠道扣款：余额不足直接失败，不跨渠道拆分。
     *
     * @param command 扣款命令
     * @return 支付结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PaymentResultDTO deduct(WalletDeductCommand command) {
        String userId = requireUserId(command.getUserId());
        String channel = WalletChannels.normalize(command.getChannel());
        BigDecimal amount = requireAmount(command.getAmount());
        ensureChannels(userId);
        PaymentEntity payment = newPayment(userId, channel, amount,
                WalletConstants.DIRECTION_DEDUCT, defaultBizType(command.getBizType(), WalletConstants.BIZ_DEPOSIT_PAY));
        payment.setOrderId(trimToNull(command.getOrderId()));
        payment.setIdempotentKey(requireIdempotentKey(command.getIdempotentKey()));
        PaymentResultDTO result = settle(payment, command.getRemark());
        log.info("wallet deduct userId={} channel={} amount={} orderId={} balanceAfter={}",
                userId, channel, amount, payment.getOrderId(), result.getBalanceAfter());
        return result;
    }

    /**
     * 渠道入账：结单划转定金 / 尾款给卖家，不改写原扣款单。
     *
     * @param command 入账命令
     * @return 支付结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PaymentResultDTO credit(WalletCreditCommand command) {
        String userId = requireUserId(command.getUserId());
        String channel = WalletChannels.normalize(command.getChannel());
        BigDecimal amount = requireAmount(command.getAmount());
        ensureChannels(userId);
        PaymentEntity payment = newPayment(userId, channel, amount,
                WalletConstants.DIRECTION_GRANT,
                defaultBizType(command.getBizType(), WalletConstants.BIZ_DEPOSIT_SETTLE));
        payment.setOrderId(trimToNull(command.getOrderId()));
        payment.setIdempotentKey(requireIdempotentKey(command.getIdempotentKey()));
        PaymentResultDTO result = settle(payment, command.getRemark());
        log.info("wallet credit userId={} channel={} amount={} orderId={} balanceAfter={}",
                userId, channel, amount, payment.getOrderId(), result.getBalanceAfter());
        return result;
    }

    /**
     * 渠道退款：原渠道加回并关联原支付单。
     *
     * @param command 退款命令
     * @return 支付结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PaymentResultDTO refund(WalletRefundCommand command) {
        String userId = requireUserId(command.getUserId());
        String channel = WalletChannels.normalize(command.getChannel());
        BigDecimal amount = requireAmount(command.getAmount());
        ensureChannels(userId);
        PaymentEntity payment = newPayment(userId, channel, amount,
                WalletConstants.DIRECTION_REFUND,
                defaultBizType(command.getBizType(), WalletConstants.BIZ_DEPOSIT_REFUND));
        payment.setOrderId(trimToNull(command.getOrderId()));
        payment.setIdempotentKey(requireIdempotentKey(command.getIdempotentKey()));
        payment.setRelatedPaymentId(trimToNull(command.getRelatedPaymentId()));
        PaymentResultDTO result = settle(payment, command.getRemark());
        // 退款成功后作废原扣款单幂等键，避免「扣款→退款→再付」命中旧键白嫖
        voidRelatedDeductIdempotent(payment.getRelatedPaymentId(), result.getPaymentId());
        log.info("wallet refund userId={} channel={} amount={} orderId={} balanceAfter={}",
                userId, channel, amount, payment.getOrderId(), result.getBalanceAfter());
        return result;
    }

    /**
     * 落库支付单与流水：幂等键命中则直接回放原结果；已退款作废的扣款单不可回放。
     */
    private PaymentResultDTO settle(PaymentEntity payment, String remark) {
        PaymentEntity exists = paymentRepository.findByIdempotentKey(payment.getIdempotentKey());
        // 空值分支判断
        if (exists != null) {
            // 字段相等性校验
            if (WalletConstants.STATUS_REFUNDED.equals(exists.getStatus())) {
                throw new BusinessException(ErrorCode.PAYMENT_FAILED, "原支付已退款作废，请使用新幂等键重试");
            }
            log.info("wallet payment idempotent hit key={} paymentId={}",
                    exists.getIdempotentKey(), exists.getId());
            return toResult(exists, currentBalance(exists.getUserId(), exists.getChannel()));
        }
        boolean deduct = WalletConstants.DIRECTION_DEDUCT.equals(payment.getDirection());
        BalanceChange change = changeBalance(payment.getUserId(), payment.getChannel(), payment.getAmount(), deduct);
        payment.setId(IdUtil.nextId());
        payment.setStatus(WalletConstants.STATUS_SUCCESS);
        paymentRepository.insert(payment);
        WalletLedgerEntity ledger = new WalletLedgerEntity();
        ledger.setId(IdUtil.nextId());
        ledger.setUserId(payment.getUserId());
        ledger.setChannel(payment.getChannel());
        ledger.setBizType(payment.getBizType());
        ledger.setDirection(payment.getDirection());
        ledger.setAmount(payment.getAmount());
        ledger.setBalanceBefore(change.before());
        ledger.setBalanceAfter(change.after());
        ledger.setOrderId(payment.getOrderId());
        ledger.setPaymentId(payment.getId());
        ledger.setRemark(trimToNull(remark));
        walletLedgerRepository.insert(ledger);
        return toResult(payment, change.after());
    }

    /**
     * 将原扣款单标记为已退款，并改写幂等键腾出槽位供再次支付。
     */
    private void voidRelatedDeductIdempotent(String relatedPaymentId, String refundPaymentId) {
        // 字符串非空才继续处理
        if (!StringUtils.hasText(relatedPaymentId)) {
            return;
        }
        PaymentEntity origin = paymentRepository.getById(relatedPaymentId.trim());
        // 空值分支判断
        if (origin == null || WalletConstants.STATUS_REFUNDED.equals(origin.getStatus())) {
            return;
        }
        String oldKey = origin.getIdempotentKey();
        origin.setStatus(WalletConstants.STATUS_REFUNDED);
        // 列长 VARCHAR(128)：用原支付单 id 作废键，避免 oldKey+VOID+refundId 超长
        origin.setIdempotentKey("VOID:" + origin.getId());
        paymentRepository.updateById(origin);
        log.info("wallet void deduct paymentId={} oldKey={} newKey={}",
                origin.getId(), oldKey, origin.getIdempotentKey());
    }

    /**
     * 乐观锁调整渠道余额，版本冲突时重试。
     */
    private BalanceChange changeBalance(String userId, String channel, BigDecimal amount, boolean deduct) {
        // 循环处理
        for (int attempt = 1; attempt <= OPTIMISTIC_RETRY; attempt++) {
            WalletChannelEntity entity = walletChannelRepository.findByUserAndChannel(userId, channel);
            // 空值分支判断
            if (entity == null) {
                throw new BusinessException(ErrorCode.WALLET_NOT_FOUND, "钱包渠道未初始化");
            }
            BigDecimal before = MoneyUtil.scale(entity.getBalance());
            // 业务条件分支
            if (deduct && before.compareTo(amount) < 0) {
                throw new BusinessException(ErrorCode.WALLET_INSUFFICIENT,
                        WalletChannels.label(channel) + "余额不足");
            }
            BigDecimal after = MoneyUtil.scale(deduct ? before.subtract(amount) : before.add(amount));
            // 业务条件分支
            if (walletChannelRepository.updateBalanceOptimistic(entity.getId(), after, entity.getVersion()) > 0) {
                return new BalanceChange(before, after);
            }
            log.warn("wallet balance version conflict userId={} channel={} attempt={}", userId, channel, attempt);
        }
        throw new BusinessException(ErrorCode.PAYMENT_FAILED, "账户余额更新繁忙，请重试");
    }

    private BigDecimal currentBalance(String userId, String channel) {
        WalletChannelEntity entity = walletChannelRepository.findByUserAndChannel(userId, channel);
        return entity == null ? MoneyUtil.scale(BigDecimal.ZERO) : MoneyUtil.scale(entity.getBalance());
    }

    private PaymentEntity newPayment(String userId, String channel, BigDecimal amount,
                                     String direction, String bizType) {
        PaymentEntity payment = new PaymentEntity();
        payment.setUserId(userId);
        payment.setChannel(channel);
        payment.setAmount(amount);
        payment.setDirection(direction);
        payment.setBizType(bizType);
        return payment;
    }

    private PaymentResultDTO toResult(PaymentEntity payment, BigDecimal balanceAfter) {
        return PaymentResultDTO.builder()
                .paymentId(payment.getId())
                .userId(payment.getUserId())
                .orderId(payment.getOrderId())
                .channel(payment.getChannel())
                .amount(MoneyUtil.scale(payment.getAmount()))
                .direction(payment.getDirection())
                .bizType(payment.getBizType())
                .status(payment.getStatus())
                .balanceAfter(balanceAfter)
                .build();
    }

    private WalletLedgerDTO toLedgerDto(WalletLedgerEntity row) {
        return WalletLedgerDTO.builder()
                .id(row.getId())
                .channel(row.getChannel())
                .channelLabel(WalletChannels.label(row.getChannel()))
                .bizType(row.getBizType())
                .direction(row.getDirection())
                .amount(MoneyUtil.scale(row.getAmount()))
                .balanceBefore(MoneyUtil.scale(row.getBalanceBefore()))
                .balanceAfter(MoneyUtil.scale(row.getBalanceAfter()))
                .orderId(row.getOrderId())
                .paymentId(row.getPaymentId())
                .remark(row.getRemark())
                .createTime(row.getCreateTime())
                .build();
    }

    private String defaultBizType(String bizType, String fallback) {
        return StringUtils.hasText(bizType) ? bizType.trim() : fallback;
    }

    private String requireIdempotentKey(String idempotentKey) {
        // 字符串非空才继续处理
        if (!StringUtils.hasText(idempotentKey)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "幂等键不能为空");
        }
        return idempotentKey.trim();
    }

    private String requireUserId(String userId) {
        // 字符串非空才继续处理
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "用户 id 不能为空");
        }
        return userId.trim();
    }

    private BigDecimal requireAmount(BigDecimal amount) {
        BigDecimal value = MoneyUtil.scale(amount);
        // 业务条件分支
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "金额必须大于 0");
        }
        return value;
    }

    private String trimToNull(String text) {
        return StringUtils.hasText(text) ? text.trim() : null;
    }

    /**
     * 余额变动前后快照。
     *
     * @author wei yz
     */
    private record BalanceChange(BigDecimal before, BigDecimal after) {
    }
}
