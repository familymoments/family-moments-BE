package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.user.model.PostUserReq;
import com.spring.familymoments.domain.user.model.PostUserRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.spring.familymoments.config.BaseResponseStatus.*;
import static com.spring.familymoments.utils.ValidationRegex.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 회원 가입 API
     * [POST] /users/sign-up
     * @return BaseResponse<UserSaveRequestDto>
     */
    @ResponseBody
    @PostMapping("/users/sign-up")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
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
        /*if(!isRegexBirth(postUserReq.getBirthDate())) {
            return new BaseResponse<>(POST_USERS_INVALID_BIRTH);
        }*/
        //닉네임
        if(postUserReq.getNickname() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        }
        if(!isRegexNickName(postUserReq.getNickname())) {
            return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
        }

        PostUserRes postUserRes = userService.createUser(postUserReq);
        return new BaseResponse<>(postUserRes);
    }
}
