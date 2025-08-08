package com.dddd.authservice.service;

import com.dddd.authservice.dto.CreateUserRequest;
import com.dddd.authservice.dto.UpdateUserRequest;
import com.dddd.authservice.dto.UserResponse;
import com.dddd.authservice.entity.User;
import com.dddd.authservice.exception.BusinessException;
import com.dddd.authservice.repository.UserRepository;
import com.dddd.authservice.util.PasswordEncoderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // 1. 创建用户 - 成功
    @Test
    void testCreateUserSuccess() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("123456");
        request.setRole("admin");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());

        String result = adminUserService.createUser(request);
        assertEquals("User created successfully", result);
        verify(userRepository).save(any(User.class));
    }

    // 2. 创建用户 - 用户名已存在
    @Test
    void testCreateUserUsernameExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("existuser");
        request.setEmail("new@example.com");

        when(userRepository.findByUsername("existuser")).thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> adminUserService.createUser(request));
    }

    // 3. 创建用户 - 邮箱已存在
    @Test
    void testCreateUserEmailExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setEmail("exist@example.com");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("exist@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> adminUserService.createUser(request));
    }

    // 4. 删除用户 - 成功
    @Test
    void testDeleteUserSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);
        String result = adminUserService.deleteUser(1L);
        assertEquals("User deleted successfully", result);
        verify(userRepository).deleteById(1L);
    }

    // 5. 删除用户 - 不存在
    @Test
    void testDeleteUserNotFound() {
        when(userRepository.existsById(2L)).thenReturn(false);
        assertThrows(BusinessException.class, () -> adminUserService.deleteUser(2L));
    }

    // 6. 更新用户 - 成功
    @Test
    void testUpdateUserSuccess() {
        User user = new User();
        user.setUserId(1L);
        user.setRole(User.Role.user);
        user.setSubscriptionStatus(User.SubscriptionStatus.expired);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("updated@example.com");
        request.setRole("admin");
        request.setSubscriptionStatus("active");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String result = adminUserService.updateUser(1L, request);
        assertEquals("User updated successfully", result);
        verify(userRepository).save(user);
    }

    // 7. 更新用户 - 不存在
    @Test
    void testUpdateUserNotFound() {
        UpdateUserRequest request = new UpdateUserRequest();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> adminUserService.updateUser(99L, request));
    }

    // 8. 搜索用户 - 返回分页
    @Test
    void testSearchUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User();
        user.setUserId(1L);
        user.setUsername("abc");
        user.setEmail("a@b.com");
        user.setRole(User.Role.user);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);

        Page<UserResponse> response = adminUserService.searchUsers("a", null, null, null, 0, 10);
        assertEquals(1, response.getContent().size());
        assertEquals("abc", response.getContent().get(0).getUsername());
    }
}
