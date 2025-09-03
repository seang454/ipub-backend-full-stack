package com.istad.docuhub.feature.user;


import com.istad.docuhub.feature.user.dto.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
import java.util.Map;

public interface UserService {
     UserResponse register(UserCreateDto userCreateDto);
     AuthResponse login(String username, String password);
     List<UserResponse> getAllUsers();
     UserResponse getSingleUser(String id);
    List<UserResponse> searchUserByUsername(String username);
    void deleteUser(String userId);
    void updateUser(String userUuid, UpdateUserDto updateUserDto);
    UpdateUserImageDto updateImageUrl(String imageUrl, String userUuid);
    Map<String, Object> getValidTokens(OAuth2AuthorizedClient client, OidcUser oidcUser);
    List<UserResponse> getAllPublicUser();
    List<UserResponse> getAllStudent();
    List<UserResponse> getAllMentor();
    void promoteAsStudent(String studentUuid);
    void promoteAsMentor(String mentorUuid);
    CurrentUser getCurrentUserSub();
}
