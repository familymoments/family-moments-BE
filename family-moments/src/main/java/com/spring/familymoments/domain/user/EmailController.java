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
     * 본인인증 메일 전송 API - 회원 가입
     * [POST] /users/send-email
     * @return BaseResponse<String>
     */
    @NoAuthCheck
    @PostMapping("/users/send-email")
    public BaseResponse<String> sendVerificationEmailBeforeSignUp(@Valid @RequestBody PostEmailReq.sendVerificationEmail sendEmailReq)
            throws BaseException, MessagingException, UnsupportedEncodingException {
        //이메일
        if(!isRegexEmail(sendEmailReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        if(emailService.checkEmailByStatus(sendEmailReq)) {
            return new BaseResponse<>(POST_USERS_EXISTS_EMAIL);
        } else {
            String verificationCode = emailService.sendEmail(sendEmailReq.getEmail());
            // return new BaseResponse<>("사용 가능한 이메일입니다.");
            return new BaseResponse<>("입력하신 이메일로 인증 번호가 발송되었습니다. 발송된 인증 번호를 입력한 후, '인증확인' 버튼을 눌러주세요.");
        }
    }

    /**
     * 이메일 인증 API - 회원 가입
     * [POST] /users/verify-email
     * @return BaseResponse<String>
     */
    @NoAuthCheck
    @PostMapping("/users/verify-email")
    public BaseResponse<String> verifyEmailBeforeSignUp(@Valid @RequestBody PostEmailReq.sendVerificationEmail sendEmailReq) throws BaseException {
        if(!emailService.checkVerificationCodeBeforeSignUp(sendEmailReq)) {
            return new BaseResponse<>(NOT_EQUAL_VERIFICATION_CODE);
        } else {
            return new BaseResponse<>("이메일 인증이 완료되었습니다! 회원가입을 완료해주세요!");
        }
    }

    /**
     * 본인인증 메일 전송 API - 아이디/비밀번호 찾기
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

        if(!emailService.checkEmailByStatus(sendEmailReq)){
            return new BaseResponse<>(false, FIND_FAIL_USER_NAME_AND_EMAIL.getMessage(), HttpStatus.NOT_FOUND.value());
        } else {
            String verificationCode = emailService.sendEmail(sendEmailReq.getEmail());
            return new BaseResponse<>("입력하신 이메일로 인증 코드가 전송되었습니다.");
        }
    }
}
