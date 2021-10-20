package com.zml.dingauto.util;

/**
 * @author zhangmingliang
 * @version 1.0
 * @date 2021/10/14 10:57
 */
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private int errorCode;

    public BaseException(String message) {
        super(message);
        this.errorCode = -1;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
