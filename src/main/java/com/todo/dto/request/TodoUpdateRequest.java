package com.todo.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * TODO更新请求DTO：所有字段均为可选，仅传递需要修改的字段
 */
@Data
public class TodoUpdateRequest {
    // 可选：更新TODO名称
    private String name;

    // 可选：更新TODO描述
    private String description;

    // 可选：更新到期日
    private LocalDateTime dueDate;

    // 可选：更新状态（字符串形式，后台转换为枚举：NOT_STARTED/IN_PROGRESS/COMPLETED）
    private String status;

    // 可选：更新优先级（字符串形式，后台转换为枚举：LOW/MEDIUM/HIGH）
    private String priority;

    // 可选：更新标签（传新的标签ID集合，会覆盖原有标签）
    private Set<Long> tagIds;
}