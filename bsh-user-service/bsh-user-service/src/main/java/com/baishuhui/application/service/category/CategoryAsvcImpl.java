package com.baishuhui.application.service.category;

import com.baishuhui.common.constant.ErrorCode;
import com.baishuhui.common.exception.BusinessException;
import com.baishuhui.domain.category.entity.CategoryEntity;
import com.baishuhui.domain.category.repositories.ICategoryRepository;
import com.baishuhui.user.vo.category.AdminCategoryDTO;
import com.baishuhui.user.vo.category.CategoryDTO;
import com.baishuhui.user.vo.category.UpsertCategoryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 农产品品类查询与管理端维护。
 *
 * @author wei yz
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryAsvcImpl implements ICategoryAsvc {

    private final ICategoryRepository categoryRepository;

    @Override
    public List<CategoryDTO> listEnabled() {
        return categoryRepository.listEnabled().stream().map(this::toPublicDto).collect(Collectors.toList());
    }

    @Override
    public List<AdminCategoryDTO> listAll() {
        return categoryRepository.listAllOrdered().stream().map(this::toAdminDto).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AdminCategoryDTO create(UpsertCategoryRequest request) {
        validate(request);
        String name = request.getName().trim();
        if (categoryRepository.existsName(name, null)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "品类名称已存在");
        }
        CategoryEntity entity = new CategoryEntity();
        apply(entity, request, name);
        categoryRepository.insert(entity);
        log.info("category created id={} name={}", entity.getId(), entity.getName());
        return toAdminDto(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AdminCategoryDTO update(String id, UpsertCategoryRequest request) {
        validate(request);
        CategoryEntity entity = categoryRepository.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "品类不存在");
        }
        String name = request.getName().trim();
        if (categoryRepository.existsName(name, id)) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "品类名称已存在");
        }
        apply(entity, request, name);
        categoryRepository.updateById(entity);
        log.info("category updated id={} name={}", id, entity.getName());
        return toAdminDto(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String id) {
        if (!StringUtils.hasText(id)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "品类 id 不能为空");
        }
        CategoryEntity entity = categoryRepository.getById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "品类不存在");
        }
        categoryRepository.deleteById(id);
        log.info("category deleted id={}", id);
    }

    private void validate(UpsertCategoryRequest request) {
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "品类名称不能为空");
        }
        if (request.getEnabled() == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "启用状态不能为空");
        }
    }

    private void apply(CategoryEntity entity, UpsertCategoryRequest request, String name) {
        entity.setName(name);
        entity.setSortNo(request.getSortNo() == null ? 100 : request.getSortNo());
        entity.setEnabled(Boolean.TRUE.equals(request.getEnabled()) ? 1 : 0);
    }

    private CategoryDTO toPublicDto(CategoryEntity entity) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSortNo(entity.getSortNo());
        return dto;
    }

    private AdminCategoryDTO toAdminDto(CategoryEntity entity) {
        AdminCategoryDTO dto = new AdminCategoryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSortNo(entity.getSortNo());
        dto.setEnabled(entity.getEnabled() != null && entity.getEnabled() == 1);
        return dto;
    }
}
