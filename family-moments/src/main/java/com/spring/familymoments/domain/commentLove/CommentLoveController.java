package com.spring.familymoments.domain.commentLove;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.commentLove.model.CommentLoveReq;
import com.spring.familymoments.domain.user.AuthService;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

import static com.spring.familymoments.config.BaseResponseStatus.INVALID_JWT;
import static com.spring.familymoments.config.BaseResponseStatus.INVALID_USER_JWT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("commentloves")
public class CommentLoveController {

    private final CommentLoveService commentLoveService;

    private final AuthService authService;

    /**
     * 댓글 내 하트 생성
     * [POST] /commentloves
     * @return BaseResponse<String>
     */
    @PostMapping("")
    public BaseResponse<String> createPostLove(@AuthenticationPrincipal User user,
                                               @RequestBody CommentLoveReq commentLoveReq,
                                               @RequestHeader("X-AUTH-TOKEN") String requestAccessToken) throws BaseException, IllegalAccessException {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }

        try {
            commentLoveService.createLove(user, commentLoveReq);
            return new BaseResponse<>("댓글에 좋아요를 누르셨습니다!");
        } catch (NoSuchElementException e){
            return new BaseResponse<>(false, e.getMessage(), NOT_FOUND.value());
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 댓글 내 하트 삭제
     * [DELETE] /commentloves
     * @return BaseResponse<String>
     */
    @DeleteMapping("")
    public BaseResponse<String> deletePostLove(@AuthenticationPrincipal User user,
                                               @RequestBody CommentLoveReq commentLoveReq,
                                               @RequestHeader("X-AUTH-TOKEN") String requestAccessToken) throws BaseException, IllegalAccessException {

        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }

        try {
            commentLoveService.deleteLove(user, commentLoveReq);
            return new BaseResponse<>("댓글 좋아요를 취소하셨습니다.");
        } catch (NoSuchElementException e){
            return new BaseResponse<>(false, e.getMessage(), NOT_FOUND.value());
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
