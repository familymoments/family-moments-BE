package com.spring.familymoments.domain.socialInfo;

import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.NoAuthCheck;
import com.spring.familymoments.config.secret.jwt.JwtSecret;
import com.spring.familymoments.config.secret.jwt.model.TokenDto;
import com.spring.familymoments.domain.awsS3.AwsS3Service;
import com.spring.familymoments.domain.socialInfo.model.*;
import com.spring.familymoments.domain.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static com.spring.familymoments.config.BaseResponseStatus.*;
import static com.spring.familymoments.utils.ValidationRegex.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SocialUserController {
    private final SocialUserService socialUserService;
    private final UserService userService;
    private final AwsS3Service awsS3Service;
    private final long COOKIE_EXPIRATION = JwtSecret.COOKIE_EXPIRATION_TIME;

    @NoAuthCheck
    @PostMapping("/users/oauth2/social/login")
    public ResponseEntity<?> doSocialLogin(@Valid @RequestBody SocialLoginRequest request) throws Exception {
        SocialLoginOrJoinResponse socialLoginOrJoinResponse = socialUserService.doSocialLogin(request);

        TokenDto tokenDto = socialLoginOrJoinResponse.getLoginResponse().getTokenDto();

        if(tokenDto != null) {
            //RefreshToken 쿠키에 저장
            HttpCookie httpCookie = ResponseCookie.from("refresh-token", tokenDto.getRefreshToken())
                    .maxAge(COOKIE_EXPIRATION)
                    .httpOnly(true)
                    .secure(true)
                    .build();

            //로그인
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, httpCookie.toString())
                    .header("X-AUTH-TOKEN", tokenDto.getAccessToken()).build();
        }
        //회원 가입 유도
        return ResponseEntity.ok()
                .body(socialLoginOrJoinResponse.getJoinResponse());
    }
    @NoAuthCheck
    @PostMapping("/users/oauth2/social/join")
    public ResponseEntity<?> createSocialUser(@RequestPart("userJoinReq") UserJoinRequest userJoinRequest,
                                                           @RequestPart("profileImg") MultipartFile profileImage) {
        //아이디
        if (userJoinRequest.getId().isEmpty()) {
            return ResponseEntity.status(400)
                    .body(new BaseResponse<>(USERS_EMPTY_USER_ID));
        }
        if (!isRegexId(userJoinRequest.getId())) {
            return ResponseEntity.status(400)
                    .body(new BaseResponse<>(POST_USERS_INVALID_ID));
        }
        //아이디 중복 체크
        if (userService.checkDuplicateId(userJoinRequest.getId())) {
            return ResponseEntity.status(400)
                    .body(new BaseResponse<>(POST_USERS_EXISTS_ID));
        }
        //이름
        if (userJoinRequest.getName().isEmpty()) {
            return ResponseEntity.status(400)
                    .body(new BaseResponse<>(POST_USERS_EMPTY_NAME));
        }
        //생년월일
        if (userJoinRequest.getStrBirthDate().isEmpty()) {
            return ResponseEntity.status(400)
                    .body(new BaseResponse<>(POST_USERS_EMPTY_BIRTH));
        }
        if (!isRegexBirth(userJoinRequest.getStrBirthDate())) {
            return ResponseEntity.status(400)
                    .body(new BaseResponse<>(POST_USERS_INVALID_BIRTH));
        }
        //닉네임
        if (userJoinRequest.getNickname().isEmpty()) {
            return ResponseEntity.status(400)
                    .body(new BaseResponse<>(POST_USERS_EMPTY_NICKNAME));
        }
        if (!isRegexNickName(userJoinRequest.getNickname())) {
            return ResponseEntity.status(400)
                    .body(new BaseResponse<>(POST_USERS_INVALID_NICKNAME));
        }
        //프로필 사진
        String fileUrl = null;
        if (userJoinRequest.getProfileImg() == null) {
            fileUrl = awsS3Service.uploadImage(profileImage);
        }
        userJoinRequest.setProfileImg(fileUrl);

        TokenDto tokenDto = socialUserService.createSocialUser(userJoinRequest);

        if(tokenDto != null) {
            //RefreshToken 쿠키에 저장
            HttpCookie httpCookie = ResponseCookie.from("refresh-token", tokenDto.getRefreshToken())
                    .maxAge(COOKIE_EXPIRATION)
                    .httpOnly(true)
                    .secure(true)
                    .build();

            //로그인
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, httpCookie.toString())
                    .header("X-AUTH-TOKEN", tokenDto.getAccessToken()).build();
        }
        return ResponseEntity.ok()
                .body(new BaseResponse<>(TOKEN_RESPONSE_ERROR));
    }
}
