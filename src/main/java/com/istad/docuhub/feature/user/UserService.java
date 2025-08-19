package com.istad.docuhub.feature.user;

import com.istad.docuhub.feature.user.dto.AuthResponse;
import com.istad.docuhub.feature.user.dto.UserCreateDto;
import com.istad.docuhub.feature.user.dto.UserResponse;

public interface UserService {
    public UserResponse register(UserCreateDto userCreateDto);
    public AuthResponse login(String username, String password);
}
