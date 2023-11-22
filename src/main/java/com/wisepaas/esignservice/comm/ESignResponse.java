package com.wisepaas.esignservice.comm;

import java.lang.reflect.Type;
import java.util.Objects;


public class ESignResponse<T> {
    private int code;
    private String message;
    private T data;

    public ESignResponse() {
        this.code = 0;
        this.message = "success";
        this.data = null;
    }

    public ESignResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T, R extends ESignResponse<T>> R fromJson(String json, Type type) {
        return ObjectMapperUtils.fromJson(json, type);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String toJson() {
        if (Objects.isNull(data)) {
            return "{\"code\":" + code + ",\"message\":\"" + message + "\", \"data\":{} }";
        }
        return ObjectMapperUtils.toJson(this);
    }
}
