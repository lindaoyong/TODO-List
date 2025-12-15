package com.todo.repository;

import com.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 根据用户ID查询TODO
    List<Todo> findByUserId(Long userId);
    // 根据团队ID查询TODO
    List<Todo> findByTeamId(Long teamId);
}