package com.istad.docuhub.feature.studentDetail.mapper;


import com.istad.docuhub.domain.StudentDetail;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.studentDetail.dto.StudentDetailRequest;
import com.istad.docuhub.feature.studentDetail.dto.StudentDetailResponse;
import com.istad.docuhub.feature.studentDetail.dto.UpdateStudentDetailRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    // MapStruct will NOT touch the user field
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "user", ignore = true)
    StudentDetail toEntity(StudentDetailRequest request);

    @Mapping(target = "userUuid", source = "user.uuid")
    StudentDetailResponse toResponse(StudentDetail studentDetail);

    // updatePartial will also ignore user
    @Mapping(target = "user", ignore = true)
    void updatePartial(@MappingTarget StudentDetail studentDetail,
                       UpdateStudentDetailRequest request);
}
