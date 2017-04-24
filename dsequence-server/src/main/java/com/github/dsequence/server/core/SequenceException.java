package com.github.dsequence.server.core;

/**
 * 简介
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/1
 */
public class SequenceException extends RuntimeException {

    private static final long serialVersionUID = -3644532090572086879L;

    public String getCode() {
        return code;
    }

    private final String code;

    public SequenceException(ErrorCodeEnum code) {
        super(code.getMessage());
        this.code = code.getCode();
    }

    public SequenceException(ErrorCodeEnum code, Throwable throwable) {
        super(throwable);
        this.code = code.getCode();
    }

    public SequenceException(ErrorCodeEnum code, String message) {
        super(message);
        this.code = code.getCode();
    }

    public SequenceException(ErrorCodeEnum code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code.getCode();
    }

}
