package com.spring.familymoments.domain.comment;

import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.comment.model.GetCommentsRes;
import com.spring.familymoments.domain.comment.model.PatchCommentReq;
import com.spring.familymoments.domain.comment.model.PostCommentReq;
import com.spring.familymoments.domain.post.model.ContentReportReq;
import com.spring.familymoments.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Tag(name = "Comment", description = "댓글 API Document")
public class CommentController {
    private final CommentService commentService;

    /**
     * 댓글 생성 API
     * [POST] /comments?postId={게시글인덱스}
     * @return BaseResponse<String>
     */
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "댓글 생성", description = "댓글을 생성합니다.")
    public BaseResponse<String> createComment(
            @AuthenticationPrincipal @Parameter(hidden = true) User user,
            @RequestParam("postId") Long postId,
            @Valid @RequestPart PostCommentReq postCommentReq) {
        commentService.createComment(user, postId, postCommentReq);
        return new BaseResponse<>("댓글이 업로드되었습니다.");
    }

    /**
     * 특정 게시물의 댓글 목록 조회 API
     * [POST] /comments?postId={게시글인덱스}
     *
     * @return BaseResponse<GetCommentsRes>
     */
    @GetMapping("")
    @Operation(summary = "특정 게시물의 댓글 목록 조회", description = "특정 게시물의 댓글 목록을 조회합니다.")
    public BaseResponse<List<GetCommentsRes>> getCommentsByPostId(@AuthenticationPrincipal @Parameter(hidden = true) User user,
                                                                  @RequestParam("postId") Long postId) {
        List<GetCommentsRes> getCommentsRes = commentService.getCommentsByPostId(user, postId);
        return new BaseResponse<>(getCommentsRes);
    }

    /**
     * 댓글 삭제 API
     * [DELETE] /comments/:{댓글인덱스}
     *
     * @return BaseResponse<String>
     */
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public BaseResponse<String> deleteComment(
            @AuthenticationPrincipal @Parameter(hidden = true) User user,
            @PathVariable Long commentId) {
        commentService.deleteComment(user, commentId);
        return new BaseResponse<>("댓글이 삭제되었습니다.");
    }

    /**
     * 댓글 수정 API
     * [Patch] /comments/:{댓글인덱스}
     *
     * @return BaseResponse<String>
     */
    @PatchMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    public BaseResponse<String> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody PatchCommentReq patchCommentReq) {
        commentService.updateComment(commentId, patchCommentReq);
        return new BaseResponse<>("댓글이 수정되었습니다.");
    }

    /**
     * 댓글 신고 API
     * [POST] /comments/report/{commentId}
     */
    @PostMapping("/report/{commentId}")
    public BaseResponse<String> reportComment(@AuthenticationPrincipal @Parameter(hidden = true) User user,
                                              @PathVariable Long commentId, @RequestBody ContentReportReq contentReportReq) {
        commentService.reportComment(user, commentId, contentReportReq);
        return new BaseResponse<>("댓글을 신고했습니다.");
    }
}
