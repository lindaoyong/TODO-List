package com.todo.builder;

import com.todo.entity.Todo;
import com.todo.entity.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// TODO构建器（Builder模式）
public class TodoBuilder {
    private String name;
    private String description;
    private LocalDateTime dueDate;
    private Todo.TodoStatus status;
    private Todo.TodoPriority priority;
    private User user;
    private Set<com.todo.entity.Tag> tags;

    public TodoBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TodoBuilder description(String description) {
        this.description = description;
        return this;
    }

    public TodoBuilder dueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public TodoBuilder status(Todo.TodoStatus status) {
        this.status = status;
        return this;
    }

    public TodoBuilder priority(Todo.TodoPriority priority) {
        this.priority = priority;
        return this;
    }

    public TodoBuilder user(User user) {
        this.user = user;
        return this;
    }

    public TodoBuilder tags(Set<com.todo.entity.Tag> tags) {
        this.tags = tags;
        return this;
    }

    // 构建方法
    public Todo build() {
        Todo todo = new Todo();
        todo.setName(this.name);
        todo.setDescription(this.description);
        todo.setDueDate(this.dueDate);
        todo.setStatus(this.status != null ? this.status : Todo.TodoStatus.NOT_STARTED);
        todo.setPriority(this.priority != null ? this.priority : Todo.TodoPriority.MEDIUM);
        todo.setUser(this.user);
        todo.setTags(this.tags != null ? this.tags : new HashSet<>());
        return todo;
    }
}