package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.secret.jwt.JwtSecret;
import com.spring.familymoments.config.secret.jwt.model.TokenDto;
import com.spring.familymoments.domain.user.model.PostLoginReq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.NoSuchElementException;

import static com.spring.familymoments.config.BaseResponseStatus.FAILED_TO_LOGIN;
import static com.spring.familymoments.config.BaseResponseStatus.INVALID_USER_JWT;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final long COOKIE_EXPIRATION = JwtSecret.COOKIE_EXPIRATION_TIME;
    private final AuthService authService;
    /**
     * 로그인 API -> token 발급
     * [POST] /users/log-in
     * return 200
     *        [header] Cookie : "refresh-token=e~~~" (refresh-token)
     *                 X-AUTH-TOKEN : e~~~ (access-token)
     */
    @PostMapping("/users/log-in")
    public ResponseEntity<?> login(@RequestBody PostLoginReq postLoginReq) {
        try {
            //User 등록 및 Refresh Token 저장
            TokenDto tokenDto = authService.login(postLoginReq);
            //RefreshToken 저장
            HttpCookie httpCookie = ResponseCookie.from("refresh-token", tokenDto.getRefreshToken())
                    .maxAge(COOKIE_EXPIRATION)
                    .httpOnly(true)
                    .secure(true)
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, httpCookie.toString())
                    .header("X-AUTH-TOKEN", tokenDto.getAccessToken())
                    .build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new BaseResponse(FAILED_TO_LOGIN));
        }
    }

    /**
     * 재발급 필요 여부 확인 API
     * [POST] /users/validate
     * return 401 ( -> 재발급 필요)
     * (백에서 처리하는 것으로 결론이 남)
     */
    @PostMapping("/users/validate")
    public ResponseEntity<?> validate(@RequestHeader("X-AUTH-TOKEN") String requestAccessToken) {
        if (!authService.validate(requestAccessToken)) {
            return ResponseEntity.status(HttpStatus.OK).build(); // 재발급 필요X
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 재발급 필요
        }
    }

    /**
     * 토큰 재발급 API
     * [POST] /users/auth/reissue
     * return 200
     *      [header] Cookie : "refresh-token=e~~~" (refresh-token)
     *               X-AUTH-TOKEN : e~~~ (access-token)
     * return 401
     *      [header] Cookie : "refresh-token=(empty)" (refresh-token)
     */
    @PostMapping("/users/reissue")
    public ResponseEntity<?> reissue(@CookieValue(name = "refresh-token") String requestRefreshToken,
                                     @RequestHeader("X-AUTH-TOKEN") String requestAccessToken) {
        try {
            TokenDto reissuedTokenDto = authService.reissue(requestAccessToken, requestRefreshToken);

            if(reissuedTokenDto != null) { //토큰 재발급 성공
                ResponseCookie responseCookie = ResponseCookie.from("refresh-token", reissuedTokenDto.getRefreshToken())
                        .maxAge(COOKIE_EXPIRATION)
                        .httpOnly(true)
                        .secure(true)
                        .build();
                return ResponseEntity.status(HttpStatus.OK)
                        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                        .header("X-AUTH-TOKEN", reissuedTokenDto.getAccessToken())
                        .build();
            } else { //Refresh Token 탈취 가능성
                ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                        .maxAge(0)
                        .path("/")
                        .build(); //쿠키 삭제 후 재로그인 유도
                return ResponseEntity
                        .status(471)
                        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                        .build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse(INVALID_USER_JWT));
        }
    }
    /**
     * 로그아웃 API
     * [POST] /users/log-out
     * return 200
     *       [header] Cookie : "refresh-token=(empty)" (refresh-token)
     */
    @PostMapping("/users/log-out")
    public ResponseEntity<?> logout(@RequestHeader("X-AUTH-TOKEN") String requestAccessToken) {
        try {
            authService.logout(requestAccessToken);
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                    .maxAge(0)
                    .path("/")
                    .build();
            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse(INVALID_USER_JWT));
        }
    }
}
