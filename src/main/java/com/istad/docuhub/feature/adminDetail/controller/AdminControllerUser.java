package com.istad.docuhub.feature.adminDetail.controller;


import com.istad.docuhub.feature.adminDetail.Mapping;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.UpdateUserDto;
import com.istad.docuhub.feature.user.dto.UserCreateDto;
import com.istad.docuhub.feature.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminControllerUser {

    private final UserService userService;
    private final Mapping mapping;
    @PostMapping("/users")
    public UserResponse createUser(@Valid @RequestBody UserCreateDto userCreateDto){
        return userService.register(userCreateDto);
    }

    @GetMapping("/users")
    public List<UserResponse> findAllUsers(){
        return userService.getAllUsers();
    }


    @GetMapping("/slug")
    public List<UserResponse> searchUserByUsername(@RequestParam String username){
        return userService.searchUserByUsername(username);
    }


    @GetMapping("/users/{id}")
    public UserResponse getUserById(@RequestBody String id){
        return userService.getSingleUser(id);
    }


    @PatchMapping("/users/{uuid}")
    public void updateUser(@PathVariable String uuid, @RequestBody UpdateUserDto updateUserDto){
        userService.updateUser(uuid, updateUserDto);
    }


    @DeleteMapping("/users/{uuid}")
    public void deleteUserByUuid(@PathVariable String uuid){
        userService.deleteUser(uuid);
    }


}
