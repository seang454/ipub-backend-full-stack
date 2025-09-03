package com.istad.docuhub.feature.adminDetail;

import com.istad.docuhub.feature.user.dto.UserCreateDto;

public interface AdminService {
    void createStudent(UserCreateDto userCreateDto);
    void createAdviser(UserCreateDto userCreateDto);

}
