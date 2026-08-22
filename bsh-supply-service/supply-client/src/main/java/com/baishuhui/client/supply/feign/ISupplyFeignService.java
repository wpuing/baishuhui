package com.baishuhui.client.supply.feign;

import com.baishuhui.supply.vo.CompleteSupplyCommand;
import com.baishuhui.supply.vo.ConfirmSupplyCommand;
import com.baishuhui.supply.vo.LockSupplyCommand;
import com.baishuhui.supply.vo.UnlockSupplyCommand;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.common.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 供应服务 Feign 客户端契约。
 *
 * @author wei yz
 */
@FeignClient(name = "bsh-supply-service", contextId = "supplyFeignClient", url = "${bsh.services.supply:}")
public interface ISupplyFeignService {

    @GetMapping("/api/consumer/supplies/{id}")
    Result<SupplyInfoDTO> detail(@PathVariable("id") String id);

    @GetMapping("/api/consumer/supplies")
    Result<List<SupplyInfoDTO>> listPublished();

    @GetMapping("/api/consumer/supplies/all")
    Result<List<SupplyInfoDTO>> listAll();

    @PostMapping("/internal/supply/{id}/reserve")
    Result<SupplyInfoDTO> reserve(@PathVariable("id") String id, @RequestBody LockSupplyCommand command);

    @PostMapping("/internal/supply/{id}/lock")
    Result<SupplyInfoDTO> lock(@PathVariable("id") String id, @RequestBody LockSupplyCommand command);

    @PostMapping("/internal/supply/{id}/confirm")
    Result<Void> confirm(@PathVariable("id") String id, @RequestBody ConfirmSupplyCommand command);

    @PostMapping("/internal/supply/{id}/complete")
    Result<SupplyInfoDTO> complete(@PathVariable("id") String id, @RequestBody CompleteSupplyCommand command);

    @PostMapping("/internal/supply/{id}/unlock")
    Result<Void> unlock(@PathVariable("id") String id, @RequestBody UnlockSupplyCommand command);
}
