package com.baishuhui.application.service.category;

import com.baishuhui.user.vo.category.CategoryDTO;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用服务接口（原 ICategoryAsvc）。
 *
 * @author wei yz
 */
public interface ICategoryAsvc {
    List<CategoryDTO> listEnabled();
}
