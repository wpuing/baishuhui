package com.baishuhui.domain.supply.entity;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.ddd.AggregateRoot;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.domain.supply.event.SupplyCompletedEvent;
import com.baishuhui.domain.supply.event.SupplyLockedEvent;
import com.baishuhui.domain.supply.event.SupplyPublishedEvent;
import com.baishuhui.domain.supply.entity.vo.ImageList;
import com.baishuhui.domain.supply.entity.vo.ProductSpec;
import com.baishuhui.supply.constant.SupplyStatusConstants;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 供应信息聚合根：农户发布菜地农产品 → 下定金预定 → 定金支付锁定 → 确认采购 → 结单完成/售罄。
 *
 * @author wei yz
 */
@Getter
public class SupplyInfo extends AggregateRoot<String> {

    /** @see SupplyStatusConstants#DRAFT */
    public static final String DRAFT = SupplyStatusConstants.DRAFT;

    /** @see SupplyStatusConstants#PUBLISHED */
    public static final String PUBLISHED = SupplyStatusConstants.PUBLISHED;

    /** @see SupplyStatusConstants#RESERVING */
    public static final String RESERVING = SupplyStatusConstants.RESERVING;

    /** @see SupplyStatusConstants#LOCKED */
    public static final String LOCKED = SupplyStatusConstants.LOCKED;

    /** @see SupplyStatusConstants#COMPLETED */
    public static final String COMPLETED = SupplyStatusConstants.COMPLETED;

    /** @see SupplyStatusConstants#SOLD_OUT */
    public static final String SOLD_OUT = SupplyStatusConstants.SOLD_OUT;

    /** @see SupplyStatusConstants#CANCELLED */
    public static final String CANCELLED = SupplyStatusConstants.CANCELLED;

    private String merchantId;
    private String title;
    private String description;
    private String contactPhone;
    private String location;
    private ProductSpec spec;
    private ImageList fieldImages;
    private ImageList completionImages;
    private BigDecimal price;
    private BigDecimal depositAmount;
    private String status;
    private LocalDateTime publishTime;
    private String lockedByBuyerId;
    private String lockOrderId;

    protected SupplyInfo() {
    }

    private SupplyInfo(
            String id,
            String merchantId,
            String title,
            String description,
            String contactPhone,
            String location,
            ProductSpec spec,
            ImageList fieldImages,
            BigDecimal price,
            BigDecimal depositAmount
    ) {
        super(id);
        this.merchantId = merchantId;
        this.title = title;
        this.description = description;
        this.contactPhone = contactPhone;
        this.location = location;
        this.spec = spec;
        this.fieldImages = fieldImages == null ? ImageList.empty() : fieldImages;
        this.completionImages = ImageList.empty();
        this.price = price;
        // 未显式定金时默认成交价 10%，降低农户漏填导致无法锁单
        this.depositAmount = depositAmount == null ? price.multiply(new BigDecimal("0.1")) : depositAmount;
        this.status = DRAFT;
    }

    /**
     * 创建草稿供应并做发布前必填校验。
     */
    public static SupplyInfo create(
            String id,
            String merchantId,
            String title,
            String description,
            String contactPhone,
            String location,
            ProductSpec spec,
            ImageList fieldImages,
            BigDecimal price,
            BigDecimal depositAmount
    ) {
        // 领域入口集中校验，避免不完整供应进入后续状态机
        if (merchantId == null || merchantId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_SUPPLY, "发布人不能为空");
        }
        // 空值分支判断
        if (title == null || title.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_SUPPLY, "标题不能为空");
        }
        // 空值分支判断
        if (contactPhone == null || contactPhone.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_SUPPLY, "联系电话不能为空");
        }
        // 空值分支判断
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_SUPPLY, "价格必须大于0");
        }
        // 空值分支判断
        if (fieldImages == null || fieldImages.urls().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_SUPPLY, "请上传菜地/农产品照片");
        }
        return new SupplyInfo(id, merchantId, title, description, contactPhone, location, spec, fieldImages, price, depositAmount);
    }

    /**
     * 从持久化状态重建聚合（不触发领域事件）。
     */
    public static SupplyInfo reconstitute(
            String id,
            String merchantId,
            String title,
            String description,
            String contactPhone,
            String location,
            ProductSpec spec,
            ImageList fieldImages,
            ImageList completionImages,
            BigDecimal price,
            BigDecimal depositAmount,
            String status,
            LocalDateTime publishTime,
            String lockedByBuyerId,
            String lockOrderId
    ) {
        SupplyInfo supply = new SupplyInfo(id, merchantId, title, description, contactPhone, location, spec, fieldImages, price, depositAmount);
        supply.status = status;
        supply.publishTime = publishTime;
        supply.completionImages = completionImages == null ? ImageList.empty() : completionImages;
        supply.lockedByBuyerId = lockedByBuyerId;
        supply.lockOrderId = lockOrderId;
        return supply;
    }

    /** 农户发布到系统（草稿或已下架可上架） */
    public void publish() {
        // 字段相等性校验
        if (!DRAFT.equals(this.status) && !CANCELLED.equals(this.status)) {
            throw new BusinessException(ErrorCode.SUPPLY_STATUS_INVALID, "仅草稿或已下架可发布");
        }
        this.status = PUBLISHED;
        this.publishTime = LocalDateTime.now();
        registerEvent(new SupplyPublishedEvent(getId(), merchantId, price));
    }

    /**
     * 改货：草稿、可采购、已下架可改；预定中/锁定/已结单不可改。
     */
    public void updateContent(
            String title,
            String description,
            String contactPhone,
            String location,
            ProductSpec spec,
            ImageList fieldImages,
            BigDecimal price,
            BigDecimal depositAmount
    ) {
        // 字段相等性校验
        if (!DRAFT.equals(this.status) && !PUBLISHED.equals(this.status) && !CANCELLED.equals(this.status)) {
            throw new BusinessException(ErrorCode.SUPPLY_STATUS_INVALID, "预定中、已锁定或已结单的供应不可改货");
        }
        // 空值分支判断
        if (title == null || title.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_SUPPLY, "标题不能为空");
        }
        // 空值分支判断
        if (contactPhone == null || contactPhone.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_SUPPLY, "联系电话不能为空");
        }
        // 空值分支判断
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_SUPPLY, "价格必须大于0");
        }
        // 空值分支判断
        if (fieldImages == null || fieldImages.urls().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_SUPPLY, "请上传菜地/农产品照片");
        }
        this.title = title;
        this.description = description;
        this.contactPhone = contactPhone;
        this.location = location;
        this.spec = spec;
        this.fieldImages = fieldImages;
        this.price = price;
        this.depositAmount = depositAmount == null ? price.multiply(new BigDecimal("0.1")) : depositAmount;
    }

    /** 可采购 → 已下架（无进行中占用时） */
    public void unpublish() {
        // 字段相等性校验
        if (!PUBLISHED.equals(this.status)) {
            throw new BusinessException(ErrorCode.SUPPLY_STATUS_INVALID, "仅可采购的供应可下架");
        }
        this.status = CANCELLED;
    }

    /** 采购方下定金预定（尚未支付，仅占用货源） */
    public void reserve(String buyerId, String orderId) {
        // 字段相等性校验
        if (!PUBLISHED.equals(this.status)) {
            throw new BusinessException(ErrorCode.SUPPLY_STATUS_INVALID, "仅可采购的供应可预定");
        }
        // 空值分支判断
        if (buyerId == null || buyerId.isBlank() || orderId == null || orderId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_LOCK, "预定信息不完整");
        }
        // 发布人不能预定自己的货源
        if (buyerId.equals(this.merchantId)) {
            throw new BusinessException(ErrorCode.INVALID_TRADE, "不能拍下自己发布的供应");
        }
        this.status = RESERVING;
        this.lockedByBuyerId = buyerId;
        this.lockOrderId = orderId;
    }

    /** 采购方支付定金成功后锁定 */
    public void lockByDeposit(String buyerId, String orderId) {
        // 字段相等性校验
        if (!RESERVING.equals(this.status)) {
            throw new BusinessException(ErrorCode.SUPPLY_STATUS_INVALID, "仅预定中的供应可锁定");
        }
        // 空值分支判断
        if (buyerId == null || buyerId.isBlank() || orderId == null || orderId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_LOCK, "锁定信息不完整");
        }
        // 支付方与预定方必须一致，避免他人定金顶单
        if (!buyerId.equals(this.lockedByBuyerId) || !orderId.equals(this.lockOrderId)) {
            throw new BusinessException(ErrorCode.INVALID_LOCK, "锁定买家或订单与预定不一致");
        }
        this.status = LOCKED;
        registerEvent(new SupplyLockedEvent(getId(), buyerId, orderId, depositAmount));
    }

    /** 双方确认采购意向（仍保持锁定，等待线下履约） */
    public void confirmPurchase(String operatorId) {
        // 字段相等性校验
        if (!LOCKED.equals(this.status)) {
            throw new BusinessException(ErrorCode.SUPPLY_STATUS_INVALID, "仅锁定中的供应可确认采购");
        }
        // 空值分支判断
        if (operatorId == null || operatorId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_CONFIRM, "操作人不能为空");
        }
        // 状态仍为 LOCKED，由订单侧记录 CONFIRMED；此处校验权限主体参与
        if (!operatorId.equals(merchantId) && !operatorId.equals(lockedByBuyerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅供需双方可确认采购");
        }
    }

    /** 线下完成后传结单图，结束发布 */
    public void complete(ImageList completionPhotos, boolean soldOut) {
        // 字段相等性校验
        if (!LOCKED.equals(this.status)) {
            throw new BusinessException(ErrorCode.SUPPLY_STATUS_INVALID, "仅锁定中的供应可结单");
        }
        // 空值分支判断
        if (completionPhotos == null || completionPhotos.urls().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_COMPLETE, "请上传交易结束图片");
        }
        this.completionImages = completionPhotos;
        this.status = soldOut ? SOLD_OUT : COMPLETED;
        registerEvent(new SupplyCompletedEvent(getId(), lockedByBuyerId, lockOrderId, this.status));
    }

    /** 预定超时 / 取消 / 定金落单失败时释放占用 */
    public void unlock(String orderId) {
        // 字段相等性校验
        if (!RESERVING.equals(this.status) && !LOCKED.equals(this.status)) {
            throw new BusinessException(ErrorCode.SUPPLY_STATUS_INVALID, "仅预定中或锁定中的供应可解锁");
        }
        // 空值分支判断
        if (orderId == null || !orderId.equals(this.lockOrderId)) {
            throw new BusinessException(ErrorCode.INVALID_UNLOCK, "解锁订单不匹配");
        }
        this.status = PUBLISHED;
        this.lockedByBuyerId = null;
        this.lockOrderId = null;
    }

    /** 兼容旧字段名 */
    public ImageList getImages() {
        return fieldImages;
    }
}
