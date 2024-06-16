package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.NoAuthCheck;
import com.spring.familymoments.domain.user.model.PostEmailReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;

import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;

import static com.spring.familymoments.config.BaseResponseStatus.*;
import static com.spring.familymoments.utils.ValidationRegex.isRegexEmail;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    /**
     * 본인인증 메일 전송 API
     * [POST] /users/auth/send-email
     * @return BaseResponse<String>
     */
    @NoAuthCheck
    @PostMapping("/users/auth/send-email")
    public BaseResponse<String> sendVerificationEmail(@Valid @RequestBody PostEmailReq.sendVerificationEmail sendEmailReq)
            throws BaseException, MessagingException, UnsupportedEncodingException {

        //이메일
        if(!isRegexEmail(sendEmailReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        try{
            if(emailService.checkNameAndEmail(sendEmailReq)){
                String verificationCode = emailService.sendEmail(sendEmailReq.getName(), sendEmailReq.getEmail());
                return new BaseResponse<>("입력하신 이메일로 인증 코드가 전송되었습니다.");
            } else {
                return new BaseResponse<>(false, FIND_FAIL_USER_NAME_AND_EMAIL.getMessage(), HttpStatus.NOT_FOUND.value());
            }
        } catch (NoSuchElementException e) {
            return new BaseResponse<>(false, FIND_FAIL_USER_NAME_AND_EMAIL.getMessage(), HttpStatus.NOT_FOUND.value());
        }
    }
}
