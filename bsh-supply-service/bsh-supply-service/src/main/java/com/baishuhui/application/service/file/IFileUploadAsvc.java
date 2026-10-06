package com.baishuhui.application.service.file;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.common.util.IdUtil;
import com.baishuhui.supply.vo.FileUploadDTO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 应用服务接口（原 IFileUploadAsvc）。
 *
 * @author wei yz
 */
public interface IFileUploadAsvc {
    FileUploadDTO upload(byte[] data);
}
