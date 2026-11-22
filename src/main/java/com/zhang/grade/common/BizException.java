package com.zhang.grade.common;

import lombok.Getter;

/** 业务异常：由全局异常处理器翻译成统一响应 */
@Getter
public class BizException extends RuntimeException {
    private final int code;

    public BizException(String msg) {
        this(400, msg);
    }

    public BizException(int code, String msg) {
        super(msg);
        this.code = code;
    }
}
