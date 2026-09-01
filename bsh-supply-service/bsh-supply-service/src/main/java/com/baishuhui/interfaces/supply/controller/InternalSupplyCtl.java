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
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 供应内部调用 REST 接口。
 *
 * @author wei yz
 */
@RestController
@RequestMapping("/internal/supply")
@RequiredArgsConstructor
@Slf4j
public class InternalSupplyCtl {
    private final ISupplyAsvc supplyAsvc;

    /**
     * 下定金预定（PUBLISHED → RESERVING）。
     */
    @PostMapping("/{id}/reserve")
    public Result<SupplyInfoDTO> reserve(@PathVariable("id") String id, @Valid @RequestBody LockSupplyCommand command) {
        log.info("reserve invoked");
        return supplyAsvc.reserve(id, command);
    }

    /**
     * lock。
     */
    @PostMapping("/{id}/lock")

    public Result<SupplyInfoDTO> lock(@PathVariable("id") String id, @Valid @RequestBody LockSupplyCommand command) {
        log.info("lock invoked");
        return supplyAsvc.lock(id, command);
    }

    /**
     * confirm。
     */
    @PostMapping("/{id}/confirm")

    public Result<Void> confirm(@PathVariable("id") String id, @Valid @RequestBody ConfirmSupplyCommand command) {
        log.info("confirm invoked");
        return supplyAsvc.confirm(id, command);
    }

    /**
     * complete。
     */
    @PostMapping("/{id}/complete")

    public Result<SupplyInfoDTO> complete(@PathVariable("id") String id, @Valid @RequestBody CompleteSupplyCommand command) {
        log.info("complete invoked");
        return supplyAsvc.complete(id, command);
    }

    /**
     * unlock。
     */
    @PostMapping("/{id}/unlock")

    public Result<Void> unlock(@PathVariable("id") String id, @Valid @RequestBody UnlockSupplyCommand command) {
        log.info("unlock invoked");
        return supplyAsvc.unlock(id, command);
    }
}
