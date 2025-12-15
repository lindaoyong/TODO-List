package com.todo.repository;

import com.todo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层：封装用户相关数据库操作
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户（Spring Security 认证核心方法）
     * @param username 用户名
     * @return 封装在Optional中的用户对象（避免空指针）
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查询用户（注册时查重）
     * @param email 邮箱地址
     * @return 封装在Optional中的用户对象
     */
    Optional<User> findByEmail(String email);

    /**
     * 校验用户名是否已存在（注册时防重复）
     * @param username 用户名
     * @return 存在返回true，否则false
     */
    boolean existsByUsername(String username);

    /**
     * 校验邮箱是否已存在（注册时防重复）
     * @param email 邮箱地址
     * @return 存在返回true，否则false
     */
    boolean existsByEmail(String email);
}