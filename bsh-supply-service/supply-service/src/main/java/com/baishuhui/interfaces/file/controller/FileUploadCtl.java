package com.baishuhui.interfaces.file.controller;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.response.Result;
import com.baishuhui.application.service.file.IFileUploadAsvc;
import com.baishuhui.supply.constant.UploadConstants;
import com.baishuhui.supply.vo.FileUploadDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 供应照片上传（本地/standalone 落盘；生产由 Nginx 直传腾讯机）。
 *
 * @author wei yz
 */
@Tag(name = "文件上传")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadCtl {

    private final IFileUploadAsvc fileUploadAsvc;

    /**
     * 上传一张菜地/农产品照片，返回相对路径。
     *
     * @param file 表单字段名 file，jpg/png/webp，不超过 5MB
     * @return 相对 URL
     */
    @Operation(summary = "上传供应照片")
    @PostMapping("/upload")
    public Result<FileUploadDTO> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "请选择图片");
        }
        if (file.getSize() > UploadConstants.MAX_FILE_BYTES) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "图片不能超过 5MB");
        }
        try {
            return Result.success(fileUploadAsvc.upload(file.getBytes()));
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "读取图片失败");
        }
    }
}
