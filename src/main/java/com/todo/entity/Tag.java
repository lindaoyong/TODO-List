package com.todo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tag name cannot be blank")
    @Column(unique = true, nullable = false) // 标签名唯一，避免重复
    private String name;

    // 多对多关联：一个标签可关联多个TODO
    @ManyToMany(mappedBy = "tags")
    private Set<Todo> todos = new HashSet<>();

    // 可选：关联创建者（限定标签仅创建者可用）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}