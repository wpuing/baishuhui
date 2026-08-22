package com.baishuhui.supply.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 供应照片上传结果。
 *
 * @author wei yz
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "供应照片上传结果")
public class FileUploadDTO {

    @Schema(description = "相对路径，如 /uploads/supply/20260812/xxx.jpg")
    private String url;
}
