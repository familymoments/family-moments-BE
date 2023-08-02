package com.spring.familymoments.config.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResult {
    private boolean isSuccess;
    //자동으로 success로 바뀜
    private int code;
    private String message;

    /**
     * ex)
     * {
     *     "code" : 500,
     *     "message" : "아이디를 잘못 입력했습니다."
     *     "success" : false
     * }
     */
}
