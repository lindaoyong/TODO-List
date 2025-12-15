package com.todo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体：实现UserDetails适配Spring Security认证，关联个人TODO和所属团队
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                // 确保用户名和邮箱全局唯一
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    // 主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 用户名：非空、唯一，长度限制（避免过长/过短）
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(nullable = false)
    private String username;

    // 邮箱：非空、唯一、格式校验
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Column(nullable = false)
    private String email;

    // 密码：非空、长度限制（存储哈希值，不存明文）
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(nullable = false)
    private String password;

    // 用户全局角色：区分普通用户/系统管理员（非团队内角色）
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER; // 默认普通用户

    // 关联：用户创建的所有个人TODO（一对多，级联删除：用户删除则TODO也删除）
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Todo> todos = new HashSet<>();

    // 关联：用户加入的所有团队（多对多，关联中间表team_members）
    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    private Set<Team> teams = new HashSet<>();

    // 用户状态：是否启用（默认启用，用于账号封禁）
    @Column(nullable = false)
    private boolean enabled = true;

    // 全局角色枚举：系统级权限区分
    public enum UserRole {
        USER,   // 普通用户：仅操作个人/所属团队的TODO
        ADMIN   // 系统管理员：可管理所有用户、团队、TODO
    }

    // ------------------- Spring Security UserDetails 接口实现 -------------------
    // 获取用户权限（适配Spring Security的角色校验）
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 角色前缀必须加ROLE_，适配Spring Security的默认配置
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    // 账号是否未过期（默认true，简化实现）
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 账号是否未锁定（默认true，简化实现；可扩展为账号锁定逻辑）
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 凭证是否未过期（默认true，简化实现）
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 账号是否启用（关联enabled字段）
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}