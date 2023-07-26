package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.advice.exception.InternalServerErrorException;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static com.spring.familymoments.config.BaseResponseStatus.*;
import static com.spring.familymoments.utils.ValidationRegex.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EmailService emailService;

    /**
     * 회원 가입 API
     * [POST] /users/sign-up
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("/users/sign-up")
    public BaseResponse<PostUserRes> createUser(@RequestPart("newUser") PostUserReq.joinUser postUserReq,
                                                @RequestPart("profileImg") MultipartFile profileImage) throws BaseException {
        //아이디
        if(postUserReq.getId() == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        if(!isRegexId(postUserReq.getId())) {
            return new BaseResponse<>(POST_USERS_INVALID_ID);
        }
        //비밀번호
        if(!isRegexPw(postUserReq.getPassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_PW);
        }
        //이름
        if(postUserReq.getName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        //이메일
        if(postUserReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        //생년월일
        if(!isRegexBirth(postUserReq.getStrBirthDate())) {
            return new BaseResponse<>(POST_USERS_INVALID_BIRTH);
        }
        //닉네임
        if(postUserReq.getNickname() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        }
        if(!isRegexNickName(postUserReq.getNickname())) {
            return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
        }

        PostUserRes postUserRes = userService.createUser(postUserReq, profileImage);
        log.info("[createUser]: PostUserRes 생성 완료!");
        return new BaseResponse<>(postUserRes);
    }

    /**
     * 아이디 중복 확인 API
     * [GET] /users/check-id
     * @return ResponseEntity<Boolean> -> 이미 가입된 아이디면 true, 그렇지 않으면 false
     */
    @GetMapping("/users/check-id")
    public ResponseEntity<Boolean> checkDuplicateId(@RequestParam String id) throws BaseException {
        return ResponseEntity.ok(userService.checkDuplicateId(id));
    }

    /**
     * 이메일 중복 확인 API
     * [GET] /users/check-email
     * @return ResponseEntity<Boolean> -> 이미 가입된 아이디면 true, 그렇지 않으면 false
     */
    @GetMapping("/users/check-email")
    public ResponseEntity<Boolean> checkDuplicateEmail(@RequestParam String email) throws BaseException {
        return ResponseEntity.ok(userService.checkDuplicateEmail(email));
    }

    /**
     * 로그인 API
     * [POST] /users/log-in
     * @return BaseResponse<>(postLoginRes)
     */
    @PostMapping("/users/log-in")
    public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq, HttpServletResponse response) throws InternalServerErrorException {
        PostLoginRes postLoginRes = userService.createLogin(postLoginReq, response);
        return new BaseResponse<>(postLoginRes);
    }

    /**
     * 로그아웃 API
     * [POST] /users/log-out
     * @return
     */
    @PostMapping("/users/log-out")
    public BaseResponse logout(HttpServletResponse response) throws BaseException {
        Cookie cookie = new Cookie("X-AUTH-TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        response.addCookie(cookie);
        return new BaseResponse("로그아웃 했습니다.");
    }

    /**
     * 회원정보 조회 API
     * [GET] /users/profile
     * @return BaseResponse<GetProfileRes>
     */
    @RequestMapping("/users/profile")
    public BaseResponse<GetProfileRes> readProfile(@AuthenticationPrincipal User user) {
       GetProfileRes getProfileRes = userService.readProfile(user);
       return new BaseResponse<>(getProfileRes);
    }

    /**
     * 아이디 찾기 API
     * [POST] /users/auth/find-id
     * @return BaseResponse<GetUserIdRes>
     */
    @PostMapping("/users/auth/find-id")
    public BaseResponse<GetUserIdRes> findUserId(@RequestBody PostEmailReq.sendVerificationEmail sendEmailReq)
            throws InternalServerErrorException, MessagingException, BaseException {

        GetUserIdRes getUserIdRes = emailService.findUserId(sendEmailReq);

        return new BaseResponse<>(getUserIdRes);
    }
}
