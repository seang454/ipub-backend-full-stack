package com.istad.docuhub.feature.user.mapper;

import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.user.dto.UserResponse;
import org.keycloak.representations.idm.UserRepresentation;

public class UserMapperManual {
   public static UserResponse mapUserToUserResponse(User user, UserRepresentation userRepresentation) {
        return UserResponse.builder()
                .slug(user.getSlug())
                .uuid(user.getUuid())
                .fullName(user.getFullName())
                .isUser(user.getIsUser())
                .isAdmin(user.getIsAdmin())
                .isAdvisor(user.getIsAdvisor())
                .isStudent(user.getIsStudent())
                .isActive(user.getIsActive())
                .createDate(user.getCreateDate())
                .updateDate(user.getUpdateDate())
                .userName(userRepresentation.getUsername())
                .bio(user.getBio())
                .email(userRepresentation.getEmail())
                .gender(user.getGender())
                .imageUrl(user.getImageUrl())
                .address(user.getAddress())
                .firstName(userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName())
                .status(user.getStatus())
                .telegramId(user.getTelegramId())
                .contactNumber(String.valueOf(user.getContactNumber()))
                .build();
    }
}
