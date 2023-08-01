package com.spring.familymoments.domain.postLove;

import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.postLove.model.PostLoveReq;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostLoveController {

    private final PostLoveService postLoveService;

    /**
     * 게시물 내 하트 생성
     * [POST] /postloves
     * @return BaseResponse<String>
     */
    @PostMapping("/postloves")
    public BaseResponse<String> createPostLove(@AuthenticationPrincipal User user, @RequestParam Long postId){

        PostLoveReq newPostLove = new PostLoveReq(postId);
        postLoveService.createLove(user, newPostLove);

        return new BaseResponse<>("게시글에 좋아요를 누르셨습니다!");
    }

    /**
     * 게시물 내 하트 삭제
     * [DELETE] /postloves
     * @return BaseResponse<String>
     */
    @DeleteMapping("/postloves")
    public BaseResponse<String> deletePostLove(@AuthenticationPrincipal User user, @RequestParam Long postId){

        PostLoveReq newPostLove = new PostLoveReq(postId);
        postLoveService.deleteLove(user, newPostLove);

        return new BaseResponse<>("게시글 좋아요를 취소하셨습니다.");
    }
}
