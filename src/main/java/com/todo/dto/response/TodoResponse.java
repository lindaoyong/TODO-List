package com.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime dueDate;
    private String status;
    private String priority;
    private Long userId;
    private Long teamId;
    private Set<String> tags; // 标签名称集合
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}