package com.todo.strategy.filter;

import com.todo.entity.Todo;
import java.util.List;

// 筛选策略接口
public interface TodoFilterStrategy {
    List<Todo> filter(List<Todo> todos, String filterValue);
}