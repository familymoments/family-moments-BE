package com.spring.familymoments.domain.comment;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.comment.model.PostCommentReq;
import com.spring.familymoments.domain.family.model.PostFamilyReq;
import com.spring.familymoments.domain.family.model.PostFamilyRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;

import static com.spring.familymoments.config.BaseResponseStatus.FIND_FAIL_FAMILY;
import static com.spring.familymoments.config.BaseResponseStatus.FIND_FAIL_USERNAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    /**
     * 댓글 생성 API
     * [POST] /comments/:userId?postId={게시글인덱스}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/{userId}")
    public BaseResponse<String> createComment(@PathVariable Long userId,
                                             @RequestParam("postId") Long postId,
                                              @RequestPart PostCommentReq postCommentReq){
        try{
            commentService.createComment(userId, postId, postCommentReq);
            return new BaseResponse<>("댓글이 업로드되었습니다.");
        }catch (NoSuchElementException e) {
            return new BaseResponse<>(FIND_FAIL_FAMILY);
        }catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }
}
