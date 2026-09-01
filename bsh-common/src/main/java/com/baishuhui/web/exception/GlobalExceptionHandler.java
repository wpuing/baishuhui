package com.baishuhui.web.exception;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.web.spi.ExceptionMessageCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理。未知异常只返回统一文案并打日志，避免堆栈泄露到前端。
 * 业务侧可通过 {@link ExceptionMessageCustomizer} 扩展文案，勿直接改本类。
 *
 * @author wei yz
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ObjectProvider<ExceptionMessageCustomizer> messageCustomizers;

    public GlobalExceptionHandler(ObjectProvider<ExceptionMessageCustomizer> messageCustomizers) {
        this.messageCustomizers = messageCustomizers;
    }

    /**
     * 业务异常：透传错误码与消息。
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException ex) {
        return Result.fail(ex.getCode(), ex.getMessage());
    }

    /**
     * 参数校验失败：取首个字段错误，支持 SPI 定制文案。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> {
                    String field = error.getField();
                    String defaultMsg = error.getDefaultMessage();
                    // 优先使用业务定制文案
                    for (ExceptionMessageCustomizer customizer : messageCustomizers) {
                        String customized = customizer.customizeValidationMessage(field, defaultMsg);
                        if (customized != null && !customized.isBlank()) {
                            return customized;
                        }
                    }
                    return field + ": " + defaultMsg;
                })
                .orElse("参数校验失败");
        return Result.fail(ErrorCode.VALIDATION_ERROR, msg);
    }

    /**
     * 未知异常：日志记录完整堆栈，前端仅见统一文案。
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception ex) {
        log.error("Unhandled exception", ex);
        String message = ErrorCode.INTERNAL_ERROR_MESSAGE;
        for (ExceptionMessageCustomizer customizer : messageCustomizers) {
            String customized = customizer.customizeUnknownMessage(ex);
            // 仅采纳非空定制，避免空串覆盖默认安全文案
            if (customized != null && !customized.isBlank()) {
                message = customized;
                break;
            }
        }
        return Result.fail(ErrorCode.INTERNAL_ERROR, message);
    }
}
