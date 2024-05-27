package com.spring.familymoments.domain.socialInfo;

import com.spring.familymoments.config.NoAuthCheck;
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
    public ResponseEntity<SocialLoginResponse> doSocialSdkLogin(@Parameter(description = "소셜 accesstoken") @RequestHeader("SOCIAL-TOKEN") String socialToken,
                                                                @Parameter(description = "소셜 종류") @RequestBody SocialLoginSdkRequest socialLoginSdkRequest) {
        SocialLoginResponse socialLoginResponse = socialUserService.createSocialSdkUser(socialToken, socialLoginSdkRequest);

        return ResponseEntity.ok()
                .header("X-AUTH-TOKEN", socialLoginResponse.getTokenDto().getAccessToken())
                .header("REFRESH-TOKEN", socialLoginResponse.getTokenDto().getRefreshToken())
                .body(SocialLoginResponse.of(
                        socialLoginResponse.getIsExisted(),
                        socialLoginResponse.getSnsId(),
                        socialLoginResponse.getName(),
                        socialLoginResponse.getEmail(),
                        socialLoginResponse.getStrBirthDate(),
                        socialLoginResponse.getNickname(),
                        socialLoginResponse.getPicture()
                ));
    }

    @NoAuthCheck
    @PostMapping("/users/oauth2/social/join")
    @Operation(summary = "소셜 회원가입과 로그인 (폼 입력)", description = "신규 회원 대상 : 얻은 리소스 서버 정보들과 사용자의 입력으로 유저를 등록하고 로그인(헤더로 토큰 발급)합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    public ResponseEntity<?> createSocialUser(@Parameter(description = "신규 회원의 가입 정보") @RequestPart("userJoinReq") UserJoinRequest userJoinRequest,
                                              @Parameter(description = "신규 회원의 프로필 첨부파일") @RequestPart("profileImg") MultipartFile profileImage) {
        SocialJoinResponse postLoginRes = socialUserService.createSocialUser(userJoinRequest, profileImage);

        return ResponseEntity.ok()
                .header("X-AUTH-TOKEN", postLoginRes.getTokenDto().getAccessToken())
                .header("REFRESH-TOKEN", postLoginRes.getTokenDto().getRefreshToken())
                .body(postLoginRes);
    }

}
