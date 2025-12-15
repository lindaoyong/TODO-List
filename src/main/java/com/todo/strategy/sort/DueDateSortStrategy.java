package com.todo.strategy.sort;

import com.todo.entity.Todo;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// 按到期日排序
@Component
public class DueDateSortStrategy implements TodoSortStrategy {
    @Override
    public List<Todo> sort(List<Todo> todos, String sortDirection) {
        Comparator<Todo> comparator = Comparator.comparing(Todo::getDueDate);
        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }
        return todos.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}