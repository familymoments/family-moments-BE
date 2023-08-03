package com.spring.familymoments.config.response;

import org.springframework.stereotype.Service;
@Service
public class ResponseService {
    /**
     * 실패 결과만 처리하는 메소드
     */
    public CommonResult getFailResult(boolean isSuccess, int code, String message) {
        CommonResult result = new CommonResult();
        result.setSuccess(isSuccess);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}