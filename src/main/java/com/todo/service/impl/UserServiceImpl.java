package com.todo.service.impl;

import com.todo.dto.request.UserRegisterRequest;
import com.todo.dto.response.ApiResponse;
import com.todo.dto.response.UserResponse;
import com.todo.entity.User;
import com.todo.enums.ErrorCode;
import com.todo.repository.UserRepository;
import com.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户业务层实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<UserResponse> register(UserRegisterRequest registerRequest) {
        // 1. 校验用户名是否已存在
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ApiResponse.error(ErrorCode.DATA_DUPLICATE.getCode(),"用户名已存在");
        }

        // 2. 校验邮箱是否已存在
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ApiResponse.error(ErrorCode.DATA_DUPLICATE.getCode(),"邮箱已存在");
        }

        // 3. 构建用户实体，加密密码
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        // 密码加密（BCrypt算法）
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        // 默认角色：普通用户，默认启用
        user.setRole(User.UserRole.USER);
        user.setEnabled(true);

        // 4. 保存用户到数据库
        User savedUser = userRepository.save(user);

        // 5. 返回脱敏后的用户信息
        return ApiResponse.success(UserResponse.fromEntity(savedUser),"user register success!");
    }

    @Override
    public Long getCurrentUserId(Authentication authentication) {
        // 1. 校验认证上下文是否为空（未登录时）
        if (authentication == null) {
            throw new IllegalArgumentException(ErrorCode.AUTH_FAILED.getMessage());
        }

        // 2. 获取Principal并强转为自定义的User实体
        // 前提：CustomUserDetailsService.loadUserByUsername返回的是User实体（已实现UserDetails）
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            // 兼容场景：若Principal是默认UserDetails（比如测试时），则抛明确异常
            throw new IllegalArgumentException("无法获取用户ID：当前认证主体不是User实体");
        }

        // 3. 强转为User实体，直接获取ID
        User currentUser = (User) principal;
        Long userId = currentUser.getId();

        // 4. 校验用户ID是否为空（防止数据异常）
        if (userId == null) {
            throw new IllegalArgumentException(ErrorCode.RESOURCE_NOT_FOUND.getMessage() + "：用户ID为空");
        }

        return userId;
    }
}