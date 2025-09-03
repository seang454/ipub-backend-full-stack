package com.istad.docuhub.feature.user.dto;


import lombok.Builder;

@Builder
public record CurrentUser (
        String id
){
}
