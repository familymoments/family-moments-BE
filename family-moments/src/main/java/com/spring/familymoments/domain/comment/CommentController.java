package com.spring.familymoments.domain.comment;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.comment.model.GetCommentsRes;
import com.spring.familymoments.domain.comment.model.PostCommentReq;
import com.spring.familymoments.domain.user.AuthService;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.spring.familymoments.config.BaseResponseStatus.INVALID_JWT;


@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;
    private final AuthService authService;

    /**
     * 댓글 생성 API
     * [POST] /comments?postId={게시글인덱스}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> createComment(@AuthenticationPrincipal User user,
                                              @RequestParam("postId") Long postId,
                                              @RequestPart PostCommentReq postCommentReq,
                                              @RequestHeader("X-AUTH-TOKEN") String requestAccessToken){

        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }

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
    public BaseResponse<List<GetCommentsRes>> getCommentsByPostId(@RequestParam("postId") Long postId,
                                                                  @RequestHeader("X-AUTH-TOKEN") String requestAccessToken){
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }

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
    @DeleteMapping("/{commentId}")
    public BaseResponse<String> deleteComment(@AuthenticationPrincipal User user,
                                              @PathVariable Long commentId,
                                              @RequestHeader("X-AUTH-TOKEN") String requestAccessToken){
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }

        try{
            commentService.deleteComment(user, commentId);
            return new BaseResponse<>("댓글이 삭제되었습니다.");
        }catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }
}
