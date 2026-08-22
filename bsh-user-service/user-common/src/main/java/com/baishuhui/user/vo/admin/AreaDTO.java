package com.baishuhui.user.vo.admin;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 地区树节点。
 *
 * @author wei yz
 */
@Data
public class AreaDTO {

    private String id;

    private String parentId;

    private String code;

    private String name;

    private Integer level;

    private Integer sortNo;

    private List<AreaDTO> children = new ArrayList<>();
}
