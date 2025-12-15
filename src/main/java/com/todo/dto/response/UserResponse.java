package com.todo.dto.response;

import com.todo.entity.User;
import lombok.Data;

/**
 * 用户注册/查询响应DTO（脱敏）
 */
@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role; // 角色：USER/ADMIN
    private boolean enabled; // 账号是否启用

    // 从User实体转换为响应DTO
    public static UserResponse fromEntity(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setEnabled(user.isEnabled());
        return response;
    }
}