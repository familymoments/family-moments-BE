package com.spring.familymoments.domain.comment;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.comment.model.GetCommentsRes;
import com.spring.familymoments.domain.comment.model.PostCommentReq;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    /**
     * 댓글 생성 API
     * [POST] /comments?postId={게시글인덱스}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> createComment(@AuthenticationPrincipal User user,
                                             @RequestParam("postId") Long postId,
                                              @RequestPart PostCommentReq postCommentReq){
        try{
            commentService.createComment(user, postId, postCommentReq);
            return new BaseResponse<>("댓글이 업로드되었습니다.");
        }catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }

    /**
     * 특정 게시물의 댓글 목록 조회 API
     * [POST] /comments?postId={게시글인덱스}
     * @return BaseResponse<GetCommentsRes>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetCommentsRes>> getCommentsByPostId(@RequestParam("postId") Long postId){
        try{
            List<GetCommentsRes> getCommentsRes = commentService.getCommentsByPostId(postId);
            return new BaseResponse<>(getCommentsRes);
        }catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }
}
