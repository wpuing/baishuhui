package com.baishuhui.application.service.supply;

import com.baishuhui.supply.vo.CompleteSupplyCommand;
import com.baishuhui.supply.vo.ConfirmSupplyCommand;
import com.baishuhui.supply.vo.LockSupplyCommand;
import com.baishuhui.supply.vo.PublishSupplyCommand;
import com.baishuhui.supply.vo.SupplyOwnerCommand;
import com.baishuhui.supply.vo.UnlockSupplyCommand;
import com.baishuhui.supply.vo.UpdateSupplyCommand;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.common.response.Result;

import java.util.List;

/**
 * 供应应用服务接口。
 *
 * @author wei yz
 */
public interface ISupplyAsvc {
    Result<SupplyInfoDTO> publishSupply(PublishSupplyCommand cmd);
    Result<SupplyInfoDTO> updateSupply(UpdateSupplyCommand cmd);
    Result<SupplyInfoDTO> unpublish(SupplyOwnerCommand cmd);
    Result<SupplyInfoDTO> forceUnpublish(String supplyId);
    Result<SupplyInfoDTO> republish(SupplyOwnerCommand cmd);
    Result<List<SupplyInfoDTO>> listByMerchant(String merchantId);
    Result<List<SupplyInfoDTO>> listPublished();
    Result<List<SupplyInfoDTO>> listAll();
    Result<List<SupplyInfoDTO>> listFiltered(String category, String locationKeyword, String status);

    /**
     * 运营列表：保留锁定字段，便于介入判断。
     */
    Result<List<SupplyInfoDTO>> listAdmin(String category, String locationKeyword, String status);

    Result<SupplyInfoDTO> detail(String supplyId);
    Result<SupplyInfoDTO> reserve(String supplyId, LockSupplyCommand cmd);
    Result<SupplyInfoDTO> lock(String supplyId, LockSupplyCommand cmd);
    Result<Void> confirm(String supplyId, ConfirmSupplyCommand cmd);
    Result<SupplyInfoDTO> complete(String supplyId, CompleteSupplyCommand cmd);
    Result<Void> unlock(String supplyId, UnlockSupplyCommand cmd);
}
