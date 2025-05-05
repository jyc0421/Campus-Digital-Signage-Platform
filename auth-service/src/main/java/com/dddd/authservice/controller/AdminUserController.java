package com.dddd.authservice.controller;

import com.dddd.authservice.dto.CreateUserRequest;
import com.dddd.authservice.dto.UpdateUserRequest;
import com.dddd.authservice.dto.UserResponse;
import com.dddd.authservice.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @PostMapping
    public String createUser(@RequestBody CreateUserRequest request) {
        return adminUserService.createUser(request);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        return adminUserService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public String updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return adminUserService.updateUser(id, request);
    }


    @GetMapping
    public Page<UserResponse> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String subscriptionStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return adminUserService.searchUsers(username, email, role, subscriptionStatus, page, size);
    }
}