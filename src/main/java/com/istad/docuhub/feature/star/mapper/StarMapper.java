package com.istad.docuhub.feature.star.mapper;


import com.istad.docuhub.domain.Star;
import com.istad.docuhub.feature.star.dto.StarResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StarMapper {


    @Mapping(target = "paperTitle", source = "paper.title")
    @Mapping(target = "userFullName", source = "user.fullName")
    StarResponse toStarResponse(Star star);

}
