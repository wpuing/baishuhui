package com.baishuhui.interfaces.supply.controller;

import com.baishuhui.supply.vo.CompleteSupplyCommand;
import com.baishuhui.supply.vo.ConfirmSupplyCommand;
import com.baishuhui.supply.vo.LockSupplyCommand;
import com.baishuhui.supply.vo.UnlockSupplyCommand;
import com.baishuhui.supply.vo.SupplyInfoDTO;
import com.baishuhui.application.service.supply.ISupplyAsvc;
import com.baishuhui.common.response.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 供应内部调用 REST 接口。
 *
 * @author wei yz
 */
@RestController
@RequestMapping("/internal/supply")
@RequiredArgsConstructor
public class InternalSupplyCtl {
    private final ISupplyAsvc supplyAsvc;

    /**
     * 下定金预定（PUBLISHED → RESERVING）。
     */
    @PostMapping("/{id}/reserve")
    public Result<SupplyInfoDTO> reserve(@PathVariable("id") String id, @Valid @RequestBody LockSupplyCommand command) {
        return supplyAsvc.reserve(id, command);
    }

    /**
     * lock。
     */
    @PostMapping("/{id}/lock")

    public Result<SupplyInfoDTO> lock(@PathVariable("id") String id, @Valid @RequestBody LockSupplyCommand command) {
        return supplyAsvc.lock(id, command);
    }

    /**
     * confirm。
     */
    @PostMapping("/{id}/confirm")

    public Result<Void> confirm(@PathVariable("id") String id, @Valid @RequestBody ConfirmSupplyCommand command) {
        return supplyAsvc.confirm(id, command);
    }

    /**
     * complete。
     */
    @PostMapping("/{id}/complete")

    public Result<SupplyInfoDTO> complete(@PathVariable("id") String id, @Valid @RequestBody CompleteSupplyCommand command) {
        return supplyAsvc.complete(id, command);
    }

    /**
     * unlock。
     */
    @PostMapping("/{id}/unlock")

    public Result<Void> unlock(@PathVariable("id") String id, @Valid @RequestBody UnlockSupplyCommand command) {
        return supplyAsvc.unlock(id, command);
    }
}
