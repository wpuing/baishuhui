package com.baishuhui.supply.constant;

/**
 * 供应照片上传常量。
 *
 * @author wei yz
 */
public final class UploadConstants {

    private UploadConstants() {
    }

    /** 单张图片上限 5MB */
    public static final long MAX_FILE_BYTES = 5L * 1024 * 1024;

    /** 对外访问前缀，与腾讯图床保持一致 */
    public static final String URL_PREFIX = "/uploads/";
}
