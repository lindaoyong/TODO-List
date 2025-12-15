package com.todo.strategy.sort;

import com.todo.entity.Todo;
import java.util.List;

// 排序策略接口
public interface TodoSortStrategy {
    List<Todo> sort(List<Todo> todos, String sortDirection); // asc/desc
}