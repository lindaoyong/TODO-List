package com.todo.service;

import com.todo.dto.request.UserRegisterRequest;
import com.todo.dto.response.ApiResponse;
import com.todo.dto.response.UserResponse;
import org.springframework.security.core.Authentication;

/**
 * 用户业务层接口
 */
public interface UserService {
    /**
     * 用户注册
     * @param registerRequest 注册请求参数
     * @return 注册成功的用户信息（脱敏）
     */
    ApiResponse<UserResponse> register(UserRegisterRequest registerRequest);

    /**
     * 获取当前登录用户的ID
     * @param authentication 认证上下文（Spring Security自动注入）
     * @return 当前用户的ID（Long类型）
     */
    Long getCurrentUserId(Authentication authentication);
}