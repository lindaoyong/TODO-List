package com.todo.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class TodoCreateRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    private String description;

    @NotNull(message = "Due date cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;

    private String status; // 接收字符串，后续转换为枚举
    private String priority; // 接收字符串，后续转换为枚举
    private Set<Long> tagIds; // 标签ID集合
    private Long teamId; // 可选：团队ID
}