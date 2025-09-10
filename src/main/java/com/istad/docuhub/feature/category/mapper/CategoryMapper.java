package com.istad.docuhub.feature.category.mapper;

import com.istad.docuhub.domain.Category;
import com.istad.docuhub.feature.category.dto.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toCategoryResponse(Category category);
}
