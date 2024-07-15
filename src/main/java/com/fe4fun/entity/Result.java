package com.fe4fun.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Result {
    String status;
    String msg;
    Boolean isLogin;
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    Object data;

    public Result(String status, String msg, boolean isLogin, Object data) {
        this.status = status;
        this.msg = msg;
        this.isLogin = isLogin;
        this.data = data;
    }

    public Result(String status, String msg, boolean isLogin) {
        this(status, msg, isLogin, null);
    }

    public Result(String status, String msg) {
        this(status, msg, false, null);
    }

    public Result(String status, String msg, Object data) {
        this(status, msg, false, data);
    }

    public static Result failure(String message) {
        return new Result("fail", message);
    }

    public static Result success(String message) {
        return new Result("ok", message, true);
    }

    public static Result success(String message,Boolean isLogin, Object data) {
        return new Result("ok", message,isLogin, data);
    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    public Boolean getLogin() {
        return isLogin;
    }
}
