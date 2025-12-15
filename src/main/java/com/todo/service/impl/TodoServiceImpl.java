package com.todo.service.impl;

import com.todo.builder.TodoBuilder;
import com.todo.dto.request.TodoCreateRequest;
import com.todo.dto.request.TodoUpdateRequest;
import com.todo.dto.response.TodoResponse;
import com.todo.entity.Tag;
import com.todo.entity.Todo;
import com.todo.entity.User;
import com.todo.exception.ResourceNotFoundException;
import com.todo.factory.TodoFactory;
import com.todo.repository.TagRepository;
import com.todo.repository.TodoRepository;
import com.todo.repository.UserRepository;
import com.todo.service.TodoService;
import com.todo.strategy.filter.TodoFilterStrategy;
import com.todo.strategy.sort.TodoSortStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final TodoFactory todoFactory;
    private final Map<String, TodoFilterStrategy> filterStrategies; // Spring自动注入所有FilterStrategy实现
    private final Map<String, TodoSortStrategy> sortStrategies; // Spring自动注入所有SortStrategy实现

    @Override
    @Transactional
    public TodoResponse createTodo(TodoCreateRequest request, Long userId) {
        // 1. 获取用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // 2. 获取标签（可选）
        Set<Tag> tags = new HashSet<>();
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
        }

        // 3. 工厂模式创建TODO
        Todo todo;
        if ("HIGH".equalsIgnoreCase(request.getPriority())) {
            todo = todoFactory.createHighPriorityTodo(
                    request.getName(),
                    request.getDescription(),
                    request.getDueDate(),
                    user,
                    tags
            );
        } else {
            todo = todoFactory.createRegularTodo(
                    request.getName(),
                    request.getDescription(),
                    request.getDueDate(),
                    user
            );
            // 设置标签和团队（可选）
            todo.setTags(tags);
            if (request.getTeamId() != null) {
                todo.setTeam(user.getTeams().stream()
                        .filter(team -> team.getId().equals(request.getTeamId()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Team not found or not accessible")));
            }
            // 设置状态（可选）
            if (request.getStatus() != null) {
                todo.setStatus(Todo.TodoStatus.valueOf(request.getStatus().toUpperCase()));
            }
            // 设置优先级（可选）
            if (request.getPriority() != null) {
                todo.setPriority(Todo.TodoPriority.valueOf(request.getPriority().toUpperCase()));
            }
        }

        // 4. 保存TODO
        Todo savedTodo = todoRepository.save(todo);

        // 5. 转换为DTO返回
        return mapToTodoResponse(savedTodo);
    }

    @Override
    public TodoResponse getTodoById(Long id, Long userId) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));

        // 权限校验：只能查看自己的TODO
        if (!todo.getUser().getId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to access this todo");
        }

        return mapToTodoResponse(todo);
    }

    @Override
    public List<TodoResponse> getAllTodosByUser(Long userId) {
        List<Todo> todos = todoRepository.findByUserId(userId);
        return todos.stream()
                .map(this::mapToTodoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoResponse> filterTodos(Long userId, String filterType, String filterValue) {
        // 1. 获取用户所有TODO
        List<Todo> todos = todoRepository.findByUserId(userId);

        // 2. 获取筛选策略（如status、dueDate）
        TodoFilterStrategy strategy = filterStrategies.get(filterType + "FilterStrategy");
        if (strategy == null) {
            throw new RuntimeException("Unsupported filter type: " + filterType);
        }

        // 3. 执行筛选
        List<Todo> filteredTodos = strategy.filter(todos, filterValue);

        // 4. 转换为DTO
        return filteredTodos.stream()
                .map(this::mapToTodoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TodoResponse> sortTodos(Long userId, String sortType, String sortDirection) {
        // 1. 获取用户所有TODO
        List<Todo> todos = todoRepository.findByUserId(userId);

        // 2. 获取排序策略（如dueDate、name、status）
        TodoSortStrategy strategy = sortStrategies.get(sortType + "SortStrategy");
        if (strategy == null) {
            throw new RuntimeException("Unsupported sort type: " + sortType);
        }

        // 3. 执行排序
        List<Todo> sortedTodos = strategy.sort(todos, sortDirection);

        // 4. 转换为DTO
        return sortedTodos.stream()
                .map(this::mapToTodoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TodoResponse updateTodo(Long id, TodoUpdateRequest request, Long userId) {
        // 1. 获取并校验TODO
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));
        if (!todo.getUser().getId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to update this todo");
        }

        // 2. 更新字段
        if (request.getName() != null) {
            todo.setName(request.getName());
        }
        if (request.getDescription() != null) {
            todo.setDescription(request.getDescription());
        }
        if (request.getDueDate() != null) {
            todo.setDueDate(request.getDueDate());
        }
        if (request.getStatus() != null) {
            todo.setStatus(Todo.TodoStatus.valueOf(request.getStatus().toUpperCase()));
        }
        if (request.getPriority() != null) {
            todo.setPriority(Todo.TodoPriority.valueOf(request.getPriority().toUpperCase()));
        }
        if (request.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            todo.setTags(tags);
        }

        // 3. 保存更新
        Todo updatedTodo = todoRepository.save(todo);

        // 4. 转换为DTO
        return mapToTodoResponse(updatedTodo);
    }

    @Override
    @Transactional
    public void deleteTodo(Long id, Long userId) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));
        if (!todo.getUser().getId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to delete this todo");
        }
        todoRepository.delete(todo);
    }

    // 私有方法：实体转DTO
    private TodoResponse mapToTodoResponse(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setName(todo.getName());
        response.setDescription(todo.getDescription());
        response.setDueDate(todo.getDueDate());
        response.setStatus(todo.getStatus().name());
        response.setPriority(todo.getPriority().name());
        response.setUserId(todo.getUser().getId());
        response.setTeamId(todo.getTeam() != null ? todo.getTeam().getId() : null);
        // 转换标签为名称集合
        Set<String> tagNames = todo.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
        response.setTags(tagNames);
        response.setCreatedAt(todo.getCreatedAt());
        response.setUpdatedAt(todo.getUpdatedAt());
        return response;
    }
}