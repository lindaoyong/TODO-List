package com.todo.strategy.filter;

import com.todo.entity.Todo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

// 按状态筛选
@Component
public class StatusFilterStrategy implements TodoFilterStrategy {
    @Override
    public List<Todo> filter(List<Todo> todos, String filterValue) {
        try {
            Todo.TodoStatus status = Todo.TodoStatus.valueOf(filterValue.toUpperCase());
            return todos.stream()
                    .filter(todo -> todo.getStatus() == status)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + filterValue);
        }
    }
}