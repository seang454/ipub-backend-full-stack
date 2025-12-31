package com.istad.docuhub.feature.user.mapper;

import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.user.dto.UpdateUserDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring")
public interface UseMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void  updateUser(@MappingTarget User user, UpdateUserDto updateUserDto);
}
