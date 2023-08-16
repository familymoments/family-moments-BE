package com.spring.familymoments.domain.postLove;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.postLove.model.PostLoveReq;
import com.spring.familymoments.domain.user.AuthService;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.spring.familymoments.config.BaseResponseStatus.INVALID_JWT;
import static com.spring.familymoments.config.BaseResponseStatus.INVALID_USER_JWT;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostLoveController {

    private final PostLoveService postLoveService;
    private final AuthService authService;

    /**
     * 게시물 내 하트 생성
     * [POST] /postloves
     * @return BaseResponse<String>
     */
    @PostMapping("/postloves")
    public BaseResponse<String> createPostLove(@AuthenticationPrincipal User user,
                                               @RequestHeader("X-AUTH-TOKEN") String requestAccessToken,
                                               @RequestBody PostLoveReq postLoveReq) throws BaseException {

        if (authService.validate(requestAccessToken)) {
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }

        try {
            postLoveService.createLove(user, postLoveReq);

            return new BaseResponse<>("게시글에 좋아요를 누르셨습니다!");
        } catch (BaseException e){
            return new BaseResponse<>(false, e.getMessage(), NOT_FOUND.value());
        }
    }

    /**
     * 게시물 내 하트 삭제
     * [DELETE] /postloves
     * @return BaseResponse<String>
     */
    @DeleteMapping("/postloves")
    public BaseResponse<String> deletePostLove(@AuthenticationPrincipal User user,
                                               @RequestHeader("X-AUTH-TOKEN") String requestAccessToken,
                                               @RequestBody PostLoveReq postLoveReq) throws BaseException {

        if (authService.validate(requestAccessToken)) {
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }

        try {
            postLoveService.deleteLove(user, postLoveReq);

            return new BaseResponse<>("게시글 좋아요를 취소하셨습니다.");
        } catch (BaseException e){
            return new BaseResponse<>(false, e.getMessage(), NOT_FOUND.value());
        }
    }
}
