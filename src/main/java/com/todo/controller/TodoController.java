package com.todo.controller;

import com.todo.dto.request.TodoCreateRequest;
import com.todo.dto.request.TodoUpdateRequest;
import com.todo.dto.response.ApiResponse;
import com.todo.dto.response.TodoResponse;
import com.todo.entity.User;
import com.todo.enums.ErrorCode;
import com.todo.service.TodoService;
import com.todo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Tag(name = "Todo API", description = "CRUD operations for Todo items")
public class TodoController {
    private final TodoService todoService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new todo", description = "Creates a new todo item for the authenticated user")
    public ResponseEntity<ApiResponse<TodoResponse>> createTodo(
            @Valid @RequestBody TodoCreateRequest request,
            Authentication authentication) {
        Long userId = userService.getCurrentUserId(authentication);
        TodoResponse todo = todoService.createTodo(request, userId);
        return new ResponseEntity<>(ApiResponse.success(todo, "Todo created successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get todo by ID", description = "Retrieves a todo item by its ID (only for the owner)")
    public ResponseEntity<ApiResponse<TodoResponse>> getTodoById(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = userService.getCurrentUserId(authentication);
        TodoResponse todo = todoService.getTodoById(id, userId);
        return ResponseEntity.ok(ApiResponse.success(todo, "Todo retrieved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all todos for user", description = "Retrieves all todo items for the authenticated user")
    public ResponseEntity<ApiResponse<List<TodoResponse>>> getAllTodos(Authentication authentication) {
        Long userId = userService.getCurrentUserId(authentication);
        List<TodoResponse> todos = todoService.getAllTodosByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(todos, "Todos retrieved successfully"));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter todos", description = "Filters todos by type (status/dueDate) and value")
    public ResponseEntity<ApiResponse<List<TodoResponse>>> filterTodos(
            @RequestParam String filterType,
            @RequestParam String filterValue,
            Authentication authentication) {
        Long userId = userService.getCurrentUserId(authentication);
        List<TodoResponse> todos = todoService.filterTodos(userId, filterType, filterValue);
        return ResponseEntity.ok(ApiResponse.success(todos, "Todos filtered successfully"));
    }

    @GetMapping("/sort")
    @Operation(summary = "Sort todos", description = "Sorts todos by type (dueDate/name/status) and direction (asc/desc)")
    public ResponseEntity<ApiResponse<List<TodoResponse>>> sortTodos(
            @RequestParam String sortType,
            @RequestParam(defaultValue = "asc") String sortDirection,
            Authentication authentication) {
        Long userId = userService.getCurrentUserId(authentication);
        List<TodoResponse> todos = todoService.sortTodos(userId, sortType, sortDirection);
        return ResponseEntity.ok(ApiResponse.success(todos, "Todos sorted successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update todo", description = "Updates a todo item by its ID (only for the owner)")
    public ResponseEntity<ApiResponse<TodoResponse>> updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody TodoUpdateRequest request,
            Authentication authentication) {
        Long userId = userService.getCurrentUserId(authentication);
        TodoResponse todo = todoService.updateTodo(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success(todo, "Todo updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete todo", description = "Deletes a todo item by its ID (only for the owner)")
    public ResponseEntity<ApiResponse<Void>> deleteTodo(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = userService.getCurrentUserId(authentication);
        todoService.deleteTodo(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Todo deleted successfully"));
    }
}