package com.istad.docuhub.feature.adminDetail;

import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.user.dto.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface Mapping {
    UserResponse fromUser(User user);
}
