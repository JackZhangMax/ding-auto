package com.zml.dingauto.service;

/**
 * @author zhangmingliang
 * @version 1.0
 * @date 2021/6/8 18:25
 */

public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private int errorCode;

    public BaseException(String message) {
        super(message);
    }


    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
