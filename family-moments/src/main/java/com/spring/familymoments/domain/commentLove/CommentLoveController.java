package com.spring.familymoments.domain.commentLove;

import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.commentLove.model.CommentLoveReq;
import com.spring.familymoments.domain.user.AuthService;
import com.spring.familymoments.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public BaseResponse<String> createPostLove(@AuthenticationPrincipal @Parameter(hidden = true) User user,
                                               @RequestBody CommentLoveReq commentLoveReq) {
        commentLoveService.createLove(user, commentLoveReq);
        return new BaseResponse<>("댓글에 좋아요를 누르셨습니다!");
    }

    /**
     * 댓글 내 하트 삭제
     * [DELETE] /commentloves
     * @return BaseResponse<String>
     */
    @DeleteMapping("")
    public BaseResponse<String> deletePostLove(@AuthenticationPrincipal @Parameter(hidden = true) User user,
                                               @RequestBody CommentLoveReq commentLoveReq) {
        commentLoveService.deleteLove(user, commentLoveReq);
        return new BaseResponse<>("댓글 좋아요를 취소하셨습니다.");
    }
}
