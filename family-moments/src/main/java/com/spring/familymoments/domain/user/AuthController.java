package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.NoAuthCheck;
import com.spring.familymoments.config.secret.jwt.model.TokenDto;
import com.spring.familymoments.domain.fcm.FCMService;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.PostLoginReq;
import com.spring.familymoments.domain.user.model.PostLoginRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.validation.Valid;

import static com.spring.familymoments.config.BaseResponseStatus.*;


@Controller
@RequiredArgsConstructor
@Tag(name = "User-Auth", description = "인증토큰 API Document")
public class AuthController {
    private final AuthService authService;
    private final FCMService fcmService;
    /**
     * 로그인 API -> token 발급
     * [POST] /users/log-in
     * return 200
     *        [header]
     *        REFRESH-TOKEN : e~~~ (refresh-token)
     *        X-AUTH-TOKEN : e~~~ (access-token)
     */
    @PostMapping(value = "/users/log-in", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "로그인", description = "accessToken과 refreshToken을 header로 발급하면서 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = PostLoginRes.class)))
            //@ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    public ResponseEntity<?> login(@RequestHeader(value = "FCM-Token", required = false) String fcmToken, @Valid @RequestBody PostLoginReq postLoginReq) {
        //User 등록 및 Refresh Token 저장
        TokenDto tokenDto = authService.login(postLoginReq);
        //가입된 familyId 값 넘기기 -- 임시
        PostLoginRes postLoginRes = authService.login_familyId(postLoginReq.getId());

        // FCM Token 저장
        if (fcmToken == null || fcmToken.isEmpty()) {
            return ResponseEntity.badRequest().body(new BaseResponse(FIND_FAIL_FCMTOKEN));
        }
        fcmService.saveToken(postLoginReq.getId(), fcmToken);

        return ResponseEntity.ok()
                .header("X-AUTH-TOKEN", tokenDto.getAccessToken())
                .header("REFRESH-TOKEN", tokenDto.getRefreshToken())
                .body(new BaseResponse<PostLoginRes>(postLoginRes));
    }

    /**
     * 재발급 필요 여부 확인 API
     * [POST] /users/validate
     * return 461 ( -> 재발급 필요)
     * (백에서 처리하는 것으로 결론이 남)
     */
    @PostMapping(value = "/users/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "재발급 필요 여부 확인", description = "재발급 필요 여부를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK") ,
            @ApiResponse(responseCode = "461", description = "재발급 필요")
    })
    public ResponseEntity<?> validate(@RequestHeader("X-AUTH-TOKEN") String requestAccessToken) {
        if (!authService.validate(requestAccessToken)) {
            return ResponseEntity.ok()
                    .body(new BaseResponse<String>("토큰이 정상입니다.")); //재발급 필요X
        } else {
            return ResponseEntity.status(461)
                    .body(new BaseResponse<>(INVALID_JWT)); // 재발급 필요
        }
    }

    /**
     * 토큰 재발급 API
     * [POST] /users/reissue
     * return 200
     *      [header]
     *      *        REFRESH-TOKEN : e~~~ (refresh-token)
     *      *        X-AUTH-TOKEN : e~~~ (access-token)
     * return 471
     */
    @NoAuthCheck
    @PostMapping(value = "/users/reissue", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "토큰 재발급", description = "토큰을 재발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "471", description = "재로그인 해야합니다.")
    })
    public ResponseEntity<?> reissue(@RequestHeader("REFRESH-TOKEN") String requestRefreshToken,
                                     @RequestHeader("X-AUTH-TOKEN") String requestAccessToken) {
        TokenDto reissuedTokenDto = authService.reissue(requestAccessToken, requestRefreshToken);

        if(reissuedTokenDto == null) {//Refresh Token 탈취 가능성
            return ResponseEntity.status(471)
                    .body(new BaseResponse<String>(TOKEN_REISSUE_ERROR));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .header("REFRESH-TOKEN", reissuedTokenDto.getRefreshToken())
                .header("X-AUTH-TOKEN", reissuedTokenDto.getAccessToken())
                .body(new BaseResponse<>("토큰 발급을 성공했습니다."));
    }
    /**
     * 로그아웃 API
     * [POST] /users/log-out
     * return 200
     */
    @PostMapping(value = "/users/log-out", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "로그아웃", description = "쿠키의 내용 지우면서 로그아웃합니다.")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<?> logout(@RequestHeader("X-AUTH-TOKEN") String requestAccessToken,
                                    @AuthenticationPrincipal @Parameter(hidden = true) User user) {
        authService.logout(requestAccessToken);
        fcmService.deleteToken(user.getId());     // FCM Token 삭제
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse<>(SUCCESS));
    }
}
