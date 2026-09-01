package com.baishuhui.application.service.file;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.util.IdUtil;
import com.baishuhui.supply.constant.UploadConstants;
import com.baishuhui.supply.vo.FileUploadDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 本地开发用供应照片落盘；生产由远程 Nginx 直传腾讯图床。
 *
 * @author wei yz
 */
@Slf4j
@Service
public class FileUploadAsvcImpl implements IFileUploadAsvc {

    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Value("${bsh.upload.dir:./runtime/uploads}")
    private String uploadDir;

    /**
     * 校验 jpg/png/webp 后写入 supply/yyyyMMdd 目录，返回相对 URL。
     *
     * @param data 图片字节
     * @return 相对路径，如 /uploads/supply/20260812/{id}.jpg
     */
    @Override
    public FileUploadDTO upload(byte[] data) {
        // 空值分支判断
        if (data == null || data.length == 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "请选择图片");
        }
        // 业务条件分支
        if (data.length > UploadConstants.MAX_FILE_BYTES) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "图片不能超过 5MB");
        }
        String ext = sniffExt(data);
        // 空值分支判断
        if (ext == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "仅支持 jpg/png/webp");
        }
        String day = LocalDate.now().format(DAY_FMT);
        String name = IdUtil.nextId() + ext;
        Path folder = Path.of(uploadDir).toAbsolutePath().normalize().resolve("supply").resolve(day);
        try {
            Files.createDirectories(folder);
            Files.write(folder.resolve(name), data);
        } catch (IOException ex) {
            log.error("save upload failed dir={}", folder, ex);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "保存图片失败");
        }
        String url = UploadConstants.URL_PREFIX + "supply/" + day + "/" + name;
        log.info("supply image saved url={}", url);
        return new FileUploadDTO(url);
    }

    private static String sniffExt(byte[] data) {
        // 业务条件分支
        if (data.length >= 3 && data[0] == (byte) 0xFF && data[1] == (byte) 0xD8 && data[2] == (byte) 0xFF) {
            return ".jpg";
        }
        // 业务条件分支
        if (data.length >= 8
                && data[0] == (byte) 0x89
                && data[1] == 0x50
                && data[2] == 0x4E
                && data[3] == 0x47) {
            return ".png";
        }
        // 业务条件分支
        if (data.length >= 12
                && data[0] == 'R'
                && data[1] == 'I'
                && data[2] == 'F'
                && data[3] == 'F'
                && data[8] == 'W'
                && data[9] == 'E'
                && data[10] == 'B'
                && data[11] == 'P') {
            return ".webp";
        }
        return null;
    }
}
