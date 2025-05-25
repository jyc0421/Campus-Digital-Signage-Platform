package com.dddd.authservice.service;

import com.dddd.authservice.exception.BusinessException;
import com.dddd.authservice.dto.LoginRequest;
import com.dddd.authservice.dto.LoginResponse;
import com.dddd.authservice.dto.RegisterRequest;
import com.dddd.authservice.entity.User;
import com.dddd.authservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dddd.authservice.repository.UserRepository;
import com.dddd.authservice.util.PasswordEncoderUtil;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    public String register(RegisterRequest request) {
        // 检查用户名是否重复
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "Username is already taken";
        }
        // 检查邮箱是否重复
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email is already registered";
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(PasswordEncoderUtil.encode(request.getPassword()));
        user.setContactName(request.getContactName());
        user.setPhone(request.getPhone());
        user.setCompanyName(request.getCompanyName());
        user.setAddress(request.getAddress());
        user.setRole(User.Role.user);  // 默认注册普通用户

        userRepository.save(user);

        return "Registration successful";
    }
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("User not found", 400));

        boolean matches = PasswordEncoderUtil.matches(request.getPassword(), user.getPasswordHash());
        if (!matches) {
            throw new BusinessException("Invalid credentials", 400);
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name(), user.getUserId());
        return new LoginResponse(token);
    }
}
