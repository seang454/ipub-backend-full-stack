package com.istad.docuhub.feature.user;

import com.istad.docuhub.feature.user.dto.AuthResponse;
import com.istad.docuhub.feature.user.dto.UserResponse;
import com.istad.docuhub.feature.user.dto.UserCreateDto;

import java.util.List;

public interface UserService {
     UserResponse register(UserCreateDto userCreateDto);
     AuthResponse login(String username, String password);
     List<UserResponse> getAllUsers();
     UserResponse getSingleUser(String id);
    List<UserResponse> searchUserByUsername(String username);
    void deleteUser(Integer id);

}
