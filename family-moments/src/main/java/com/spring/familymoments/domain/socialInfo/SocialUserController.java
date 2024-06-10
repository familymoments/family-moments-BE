package com.spring.familymoments.domain.socialInfo;

import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.NoAuthCheck;
import com.spring.familymoments.config.secret.jwt.model.TokenDto;
import com.spring.familymoments.domain.socialInfo.model.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "SocialInfo", description = "네이버,카카오 API Document")
public class SocialUserController {
    private final SocialUserService socialUserService;

    @NoAuthCheck
    @PostMapping("/users/oauth2/social/login/sdk")
    @Operation(summary = "소셜 로그인 (아이콘 클릭 시) - sdk version", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    public ResponseEntity<BaseResponse<SocialLoginResponse>> doSocialSdkLogin(@RequestHeader(value = "FCM-Token", required = false) String fcmToken,
                                                                              @Parameter(description = "소셜 accesstoken") @RequestHeader("SOCIAL-TOKEN") String socialToken,
                                                                              @Parameter(description = "소셜 종류") @RequestBody SocialLoginSdkRequest socialLoginSdkRequest) {
        SocialLoginDto socialLoginResponse = socialUserService.createSocialSdkUser(socialToken, fcmToken, socialLoginSdkRequest);

        TokenDto tokenDto = socialLoginResponse.getTokenDto();
        String accessToken = (tokenDto != null) ? tokenDto.getAccessToken() : null;
        String refreshToken = (tokenDto != null) ? tokenDto.getRefreshToken() : null;

        Boolean isExisted = socialLoginResponse.getIsExisted();

        return ResponseEntity.ok()
                .header("X-AUTH-TOKEN", accessToken)
                .header("REFRESH-TOKEN", refreshToken)
                .body(
                        new BaseResponse<>(SocialLoginResponse.of(
                                isExisted,
                                isExisted ? null : socialLoginResponse.getName(),
                                isExisted ? null : socialLoginResponse.getEmail(),
                                isExisted ? null : socialLoginResponse.getStrBirthDate(),
                                isExisted ? null : socialLoginResponse.getNickname(),
                                isExisted ? null : socialLoginResponse.getPicture(),
                                isExisted ? socialLoginResponse.getFamilyId() : null)
                        )
                );
    }

    @NoAuthCheck
    @PostMapping("/users/oauth2/social/join")
    @Operation(summary = "소셜 회원가입과 로그인 (폼 입력)", description = "신규 회원 대상 : 얻은 리소스 서버 정보들과 사용자의 입력으로 유저를 등록하고 로그인(헤더로 토큰 발급)합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    public ResponseEntity<BaseResponse<SocialJoinResponse>> createSocialUser(@Parameter(description = "신규 회원의 가입 정보") @RequestPart("userJoinReq") UserJoinRequest userJoinRequest,
                                              @Parameter(description = "신규 회원의 프로필 첨부파일") @RequestPart("profileImg") MultipartFile profileImage) {
        SocialJoinDto socialJoinDto = socialUserService.createSocialUser(userJoinRequest, profileImage);

        TokenDto tokenDto = socialJoinDto.getTokenDto();
        String accessToken = (tokenDto != null) ? tokenDto.getAccessToken() : null;
        String refreshToken = (tokenDto != null) ? tokenDto.getRefreshToken() : null;

        return ResponseEntity.ok()
                .header("X-AUTH-TOKEN", accessToken)
                .header("REFRESH-TOKEN", refreshToken)
                .body(
                        new BaseResponse<>(SocialJoinResponse.of(
                        socialJoinDto.getFamilyId()))
                );
    }


}
