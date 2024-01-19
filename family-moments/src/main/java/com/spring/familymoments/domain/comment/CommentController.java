package com.spring.familymoments.domain.comment;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.NoAuthCheck;
import com.spring.familymoments.domain.comment.model.GetCommentsRes;
import com.spring.familymoments.domain.comment.model.PostCommentReq;
import com.spring.familymoments.domain.user.AuthService;
import com.spring.familymoments.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Tag(name = "Comment", description = "댓글 API Document")
public class CommentController {
    private final CommentService commentService;
    private final AuthService authService;

    /**
     * 댓글 생성 API
     * [POST] /comments?postId={게시글인덱스}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @NoAuthCheck
    @PostMapping("")
    @Operation(summary = "댓글 생성", description = "댓글을 생성합니다.")
    public BaseResponse<String> createComment(
            @AuthenticationPrincipal @Parameter(hidden = true) User user,
            @RequestParam("postId") Long postId,
            @RequestPart PostCommentReq postCommentReq) {
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
    @NoAuthCheck
    @GetMapping("")
    @Operation(summary = "특정 게시물의 댓글 목록 조회", description = "특정 게시물의 댓글 목록을 조회합니다.")
    public BaseResponse<List<GetCommentsRes>> getCommentsByPostId(
            @RequestParam("postId") Long postId) {
        try{
            List<GetCommentsRes> getCommentsRes = commentService.getCommentsByPostId(postId);
            return new BaseResponse<>(getCommentsRes);
        }catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }

    /**
     * 댓글 삭제 API
     * [DELETE] /comments/:{댓글인덱스}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @NoAuthCheck
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public BaseResponse<String> deleteComment(
            @AuthenticationPrincipal @Parameter(hidden = true) User user,
            @PathVariable Long commentId){
        try{
            commentService.deleteComment(user, commentId);
            return new BaseResponse<>("댓글이 삭제되었습니다.");
        }catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }
}
