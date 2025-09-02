package com.istad.docuhub.feature.adminDetail;


import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImple {

    private final UserService userService;

}
