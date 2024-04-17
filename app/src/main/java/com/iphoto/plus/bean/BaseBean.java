package com.iphoto.plus.bean;

import java.util.List;

public class BaseBean<T>     {

    /**
     * Code : 301103
     * Message : 用户名或密码错误
     * Extras : []
     */

    private int code;
    private String message;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
