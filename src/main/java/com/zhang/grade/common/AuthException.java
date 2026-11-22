package com.zhang.grade.common;

/** 鉴权失败：401 未登录 / 403 越权 */
public class AuthException extends BizException {
    public AuthException(String msg) {
        super(401, msg);
    }

    public AuthException(int code, String msg) {
        super(code, msg);
    }
}
