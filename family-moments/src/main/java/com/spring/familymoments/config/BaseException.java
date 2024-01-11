package com.spring.familymoments.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseException extends RuntimeException  {

    private BaseResponseStatus status;
    private String message;
    private int code;

    public BaseException(BaseResponseStatus status){
        this.status = status;
    }

    public BaseException(String message, int code){
        this.message = message;
        this.code = code;
    }
}
