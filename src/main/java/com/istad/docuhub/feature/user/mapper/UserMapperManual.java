package com.istad.docuhub.feature.user.mapper;

import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.user.dto.UserResponse;
import org.keycloak.representations.idm.UserRepresentation;

public class UserMapperManual {

    public static UserResponse mapUserToUserResponse(User user, UserRepresentation userRepresentation) {

        // ✅ Safely handle Keycloak userRepresentation that may be null
        String username = "N/A";
        String email = "N/A";
        String firstName = "";
        String lastName = "";

        if (userRepresentation != null) {
            if (userRepresentation.getUsername() != null) {
                username = userRepresentation.getUsername();
            }
            if (userRepresentation.getEmail() != null) {
                email = userRepresentation.getEmail();
            }
            if (userRepresentation.getFirstName() != null) {
                firstName = userRepresentation.getFirstName();
            }
            if (userRepresentation.getLastName() != null) {
                lastName = userRepresentation.getLastName();
            }
        }

        // ✅ Return the safely built response
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
                .userName(username)
                .bio(user.getBio())
                .email(email)
                .gender(user.getGender())
                .imageUrl(user.getImageUrl())
                .address(user.getAddress())
                .firstName(firstName)
                .lastName(lastName)
                .status(user.getStatus())
                .telegramId(user.getTelegramId())
                .contactNumber(String.valueOf(user.getContactNumber()))
                .build();
    }
}
