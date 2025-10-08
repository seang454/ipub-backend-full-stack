package com.istad.docuhub.feature.user.dto;

import java.time.LocalDate;

public record UpdateUserDto (
        String userName,
        String gender,
        String email,
        String fullName,
        String firstName,
        String lastName,
        String status,
        String bio,
        String address,
        String contactNumber,
        String telegramId
){
}
