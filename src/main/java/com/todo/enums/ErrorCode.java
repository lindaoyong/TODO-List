package com.todo.enums;

import lombok.Getter;

/**
 * 全局统一错误码枚举
 * 规范：
 * - 2xx：成功
 * - 4xx：客户端错误（参数、权限、资源不存在等）
 * - 5xx：服务端错误
 */
@Getter
public enum ErrorCode {
    // ========== 成功 ==========
    SUCCESS(200, "操作成功"),

    // ========== 客户端错误 ==========
    // 参数错误
    PARAM_ERROR(400, "参数校验失败"),
    // 资源不存在
    RESOURCE_NOT_FOUND(404, "请求的资源不存在"),
    // 权限拒绝
    ACCESS_DENIED(403, "无权限访问"),
    // 数据重复（用户名/邮箱/标签名重复）
    DATA_DUPLICATE(409, "数据已存在，请勿重复创建"),
    // 认证失败（登录失败、Token无效）
    AUTH_FAILED(401, "认证失败，请重新登录"),

    // ========== 服务端错误 ==========
    SERVER_ERROR(500, "服务器内部错误，请稍后重试");

    // 错误码
    private final int code;
    // 默认提示文案
    private final String message;

    // 构造方法
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}