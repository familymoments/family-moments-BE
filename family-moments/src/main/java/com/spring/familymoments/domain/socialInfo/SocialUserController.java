package com.spring.familymoments.domain.socialInfo;

import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.NoAuthCheck;
import com.spring.familymoments.config.secret.jwt.JwtSecret;
import com.spring.familymoments.config.secret.jwt.model.TokenDto;
import com.spring.familymoments.domain.awsS3.AwsS3Service;
import com.spring.familymoments.domain.socialInfo.model.*;
import com.spring.familymoments.domain.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "SocialInfo", description = "네이버,카카오,구글 API Document")
public class SocialUserController {
    private final SocialUserService socialUserService;
    private final UserService userService;
    private final AwsS3Service awsS3Service;

    @NoAuthCheck
    @PostMapping("/users/oauth2/social/login")
    @Operation(summary = "소셜 로그인 (아이콘 클릭 시) - rest api version", description = "기존 회원 대상 : 헤더로 토큰이 반환됩니다. / 신규 회원 대상(아래 예시 해당) : 사용자로부터 허용된 리소스 서버 정보들이 반환됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = SocialLoginOrJoinResponse.JoinResponse.class)))
    })
    public ResponseEntity<?> doSocialLogin(@Valid @RequestBody SocialLoginRequest request) throws Exception {
        SocialLoginOrJoinResponse socialLoginOrJoinResponse = socialUserService.doSocialLogin(request);

        TokenDto tokenDto = socialLoginOrJoinResponse.getLoginResponse().getTokenDto();

        if(tokenDto != null) {
            return socialUserService.sendAtRtTokenInfo(tokenDto);
        }
        //회원 가입 유도
        return ResponseEntity.ok()
                .body(socialLoginOrJoinResponse.getJoinResponse());
    }

    @NoAuthCheck
    @PostMapping("/users/oauth2/social/join")
    @Operation(summary = "소셜 회원가입과 로그인 (폼 입력)", description = "신규 회원 대상 : 얻은 리소스 서버 정보들과 사용자의 입력으로 유저를 등록하고 로그인(헤더로 토큰 발급)합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    public ResponseEntity<?> createSocialUser(@Parameter(description = "신규 회원의 가입 정보") @RequestPart("userJoinReq") UserJoinRequest userJoinRequest,
                                              @Parameter(description = "신규 회원의 프로필 첨부파일") @RequestPart("profileImg") MultipartFile profileImage) {
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
            return socialUserService.sendAtRtTokenInfo(tokenDto);
        }

        return ResponseEntity.status(404)
                .body(new BaseResponse<>(TOKEN_RESPONSE_ERROR));
    }

    @NoAuthCheck
    @PostMapping("/users/oauth2/social/login/sdk")
    @Operation(summary = "소셜 로그인 (아이콘 클릭 시) - sdk version", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "481", description = "해당 소셜로 회원 가입을 해야 합니다.")
    })
    public ResponseEntity<?> doSocialSdkLogin(@RequestBody SocialLoginSdkRequest socialLoginSdkRequest) {
        TokenDto tokenDto = socialUserService.createSocialSdkUser(socialLoginSdkRequest);

        if(tokenDto != null) {
            return socialUserService.sendAtRtTokenInfo(tokenDto);
        }

        return ResponseEntity.status(404)
                .body(new BaseResponse<>(TOKEN_RESPONSE_ERROR));
    }

}
