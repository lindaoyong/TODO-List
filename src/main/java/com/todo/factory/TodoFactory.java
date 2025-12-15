package com.todo.factory;

import com.todo.builder.TodoBuilder;
import com.todo.entity.Todo;
import com.todo.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

// TODO工厂（Factory模式）
@Component
public class TodoFactory {
    // 创建普通TODO
    public Todo createRegularTodo(String name, String description, LocalDateTime dueDate, User user) {
        return new TodoBuilder()
                .name(name)
                .description(description)
                .dueDate(dueDate)
                .status(Todo.TodoStatus.NOT_STARTED)
                .priority(Todo.TodoPriority.MEDIUM)
                .user(user)
                .build();
    }

    // 创建高优先级TODO
    public Todo createHighPriorityTodo(String name, String description, LocalDateTime dueDate, User user, Set<com.todo.entity.Tag> tags) {
        return new TodoBuilder()
                .name(name)
                .description(description)
                .dueDate(dueDate)
                .status(Todo.TodoStatus.IN_PROGRESS)
                .priority(Todo.TodoPriority.HIGH)
                .user(user)
                .tags(tags)
                .build();
    }
}