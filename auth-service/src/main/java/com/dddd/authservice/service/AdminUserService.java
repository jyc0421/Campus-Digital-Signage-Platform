package com.dddd.authservice.service;

import com.dddd.authservice.dto.CreateUserRequest;
import com.dddd.authservice.dto.UpdateUserRequest;
import com.dddd.authservice.dto.UserResponse;
import com.dddd.authservice.entity.User;
import com.dddd.authservice.exception.BusinessException;
import com.dddd.authservice.repository.UserRepository;
import com.dddd.authservice.util.PasswordEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;


import java.util.ArrayList;
import java.util.List;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    public String createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(PasswordEncoderUtil.encode(request.getPassword()));
        user.setContactName(request.getContactName());
        user.setPhone(request.getPhone());
        user.setCompanyName(request.getCompanyName());
        user.setAddress(request.getAddress());
        user.setRole(User.Role.valueOf(request.getRole().toLowerCase()));

        userRepository.save(user);

        return "User created successfully";
    }

    public String deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException("User not found", 400);
        }
        userRepository.deleteById(userId);
        return "User deleted successfully";
    }
    public String updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found", 400));

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getContactName() != null) {
            user.setContactName(request.getContactName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getCompanyName() != null) {
            user.setCompanyName(request.getCompanyName());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getRole() != null) {
            user.setRole(User.Role.valueOf(request.getRole().toLowerCase()));
        }
        if (request.getSubscriptionStatus() != null) {
            user.setSubscriptionStatus(User.SubscriptionStatus.valueOf(request.getSubscriptionStatus().toLowerCase()));
        }
        if (request.getSubscriptionEnd() != null) {
            user.setSubscriptionEnd(request.getSubscriptionEnd());
        }

        userRepository.save(user);
        return "User updated successfully";
    }
    public Page<UserResponse> searchUsers(
            String username,
            String email,
            String role,
            String subscriptionStatus,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (username != null) {
                predicates.add(cb.like(root.get("username"), "%" + username + "%"));
            }
            if (email != null) {
                predicates.add(cb.like(root.get("email"), "%" + email + "%"));
            }
            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role.toUpperCase()));
            }
            if (subscriptionStatus != null) {
                predicates.add(cb.equal(root.get("subscriptionStatus"), subscriptionStatus.toUpperCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> userPage = userRepository.findAll(spec, pageable);

        return userPage.map(user -> {
            UserResponse resp = new UserResponse();
            resp.setUserId(user.getUserId());
            resp.setUsername(user.getUsername());
            resp.setEmail(user.getEmail());
            resp.setContactName(user.getContactName());
            resp.setPhone(user.getPhone());
            resp.setCompanyName(user.getCompanyName());
            resp.setAddress(user.getAddress());
            resp.setRole(user.getRole().name());
            resp.setSubscriptionStatus(user.getSubscriptionStatus() != null ? user.getSubscriptionStatus().name() : null);
            resp.setSubscriptionEnd(user.getSubscriptionEnd() != null ? user.getSubscriptionEnd() : null);
            resp.setCreatedAt(user.getCreatedAt());
            resp.setUpdatedAt(user.getUpdatedAt());
            return resp;
        });
    }
}