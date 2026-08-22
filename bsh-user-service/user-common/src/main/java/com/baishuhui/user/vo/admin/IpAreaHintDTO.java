package com.baishuhui.user.vo.admin;

import lombok.Data;

/**
 * IP 归属地提示（注册默认选中）。
 *
 * @author wei yz
 */
@Data
public class IpAreaHintDTO {

    private String clientIp;

    private String province;

    private String city;

    private String areaId;

    private String areaName;

    private String message;
}
