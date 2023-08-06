package com.spring.familymoments.domain.commentLove;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.commentLove.model.CommentLoveReq;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("commentloves")
public class CommentLoveController {

    private final CommentLoveService commentLoveService;

    /**
     * 댓글 내 하트 생성
     * [POST] /commentloves
     * @return BaseResponse<String>
     */
    @PostMapping("")
    public BaseResponse<String> createPostLove(@AuthenticationPrincipal User user,
                                               @RequestBody CommentLoveReq commentLoveReq) throws BaseException {

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
                                               @RequestBody CommentLoveReq commentLoveReq) throws BaseException {

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
