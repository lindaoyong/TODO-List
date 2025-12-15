package com.todo.controller;

import com.todo.dto.request.UserRegisterRequest;
import com.todo.dto.response.ApiResponse;
import com.todo.dto.response.UserResponse;
import com.todo.entity.User;
import com.todo.enums.ErrorCode;
import com.todo.repository.UserRepository;
import com.todo.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // JWT密钥（生产环境需配置在application.yml，此处简化）
    @Value("${jwt.secret}")
    private String jwtSecret;
    // JWT过期时间（1小时）
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody UserRegisterRequest registerRequest) {
        ApiResponse<UserResponse> response = userService.register(registerRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }
    // 登录接口：用户名+密码 → 返回JWT Token
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        // 1. 查询用户
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "用户名不存在"));
        }
        User user = userOpt.get();

        // 2. 校验密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "密码错误"));
        }

        // 3. 生成JWT Token
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        String token = Jwts.builder()
                .setSubject(user.getUsername()) // 用户名作为Token主题
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // 过期时间
                .signWith(key) // 签名
                .compact();

        // 4. 返回Token
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("username", user.getUsername());
        return ResponseEntity.ok(response);
    }

    // 登录请求DTO
    public static class  LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}