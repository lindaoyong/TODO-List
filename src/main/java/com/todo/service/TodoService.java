package com.todo.service;

import com.todo.dto.request.TodoCreateRequest;
import com.todo.dto.request.TodoUpdateRequest;
import com.todo.dto.response.TodoResponse;

import java.util.List;

public interface TodoService {
    TodoResponse createTodo(TodoCreateRequest request, Long userId);
    TodoResponse getTodoById(Long id, Long userId);
    List<TodoResponse> getAllTodosByUser(Long userId);
    List<TodoResponse> filterTodos(Long userId, String filterType, String filterValue);
    List<TodoResponse> sortTodos(Long userId, String sortType, String sortDirection);
    TodoResponse updateTodo(Long id, TodoUpdateRequest request, Long userId);
    void deleteTodo(Long id, Long userId);
}