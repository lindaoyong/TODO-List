package com.todo.dto.response;

import com.todo.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private int code;
    private String message;
    private LocalDateTime timestamp;

    // 静态工厂方法
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, ErrorCode.SUCCESS.getCode(), message, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(int code,String message) {
        return new ApiResponse<>(false, null, code, message, LocalDateTime.now());
    }
}