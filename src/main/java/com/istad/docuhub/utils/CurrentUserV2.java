package com.istad.docuhub.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class CurrentUserV2 {
    private String id;      // from sub
    private String uuid;    // custom claim in JWT (if you store it)
    private String email;   // optional
    private List<String> roles;
}
