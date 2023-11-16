package com.spring.familymoments.config.advice;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.advice.exception.InternalServerErrorException;
import com.spring.familymoments.config.response.CommonResult;
import com.spring.familymoments.config.response.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    private final ResponseService responseService;

    /**
     * default Exception
     * @return CommonResult
     * @throws Exception
     */
    /*@ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult defaultException(HttpServletRequest request, Exception e) {
        return responseService.getFailResult(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }*/

    // 이 부분 추가했습니다
    @ExceptionHandler(BaseException.class)
    protected BaseResponse<Object> handleBaseException(BaseException ex) {
        return new BaseResponse<>(ex.getStatus());
    }

    /** validation exception **/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected BaseResponse<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new BaseResponse<>(false, errorMessage, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult userNotFoundException(HttpServletRequest request, InternalServerErrorException e) {
        return responseService.getFailResult(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

}
