package com.todo.repository;

import com.todo.entity.Tag;
import com.todo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 标签数据访问层：封装标签相关数据库操作，所有方法均限定用户维度
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 查询指定用户的所有标签（仅返回当前用户创建的标签，避免越权）
     * @param userId 用户ID
     * @return 该用户的标签列表
     */
    List<Tag> findByUserId(Long userId);

    /**
     * 校验指定用户是否已创建过该名称的标签（避免同一用户标签重名）
     * @param name 标签名称
     * @param userId 用户ID
     * @return 存在返回true，否则false
     */
    boolean existsByNameAndUserId(String name, Long userId);

    /**
     * 根据标签名称+用户ID查询标签（精准匹配用户私有标签）
     * @param name 标签名称
     * @param userId 用户ID
     * @return 封装在Optional中的标签对象
     */
    Optional<Tag> findByNameAndUserId(String name, Long userId);

    /**
     * 批量查询指定用户的标签（根据标签ID集合+用户ID，避免查询到其他用户的标签）
     * @param ids 标签ID集合
     * @param userId 用户ID
     * @return 该用户名下的指定标签列表
     */
    List<Tag> findByIdInAndUserId(Iterable<Long> ids, Long userId);
}