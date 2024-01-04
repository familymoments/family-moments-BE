package com.spring.familymoments.domain.postLove;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.postLove.model.PostLoveReq;
import com.spring.familymoments.domain.user.AuthService;
import com.spring.familymoments.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.spring.familymoments.config.BaseResponseStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "PostLove", description = "게시글 좋아요 API Document")
public class PostLoveController {

    private final PostLoveService postLoveService;
    private final AuthService authService;

    /**
     * 게시물 내 하트 생성
     * [POST] /postloves
     * @return BaseResponse<String>
     */
    @PostMapping("/postloves")
    @Operation(summary = "게시물 내 하트 생성", description = "게시물에 좋아요를 남기는 API 입니다.")
    public BaseResponse<String> createPostLove(@AuthenticationPrincipal @Parameter(hidden=true) User user,
                                               @Parameter(description = "좋아요를 남길 게시글의 정보")
                                               @RequestBody PostLoveReq postLoveReq) {
        try {
            postLoveService.createLove(user, postLoveReq);

            return new BaseResponse<>("게시글에 좋아요를 누르셨습니다!");
        } catch (BaseException e){
            return new BaseResponse<>(POSTLOVE_ALREADY_EXISTS);
        }
    }

    /**
     * 게시물 내 하트 삭제
     * [DELETE] /postloves
     * @return BaseResponse<String>
     */
    @DeleteMapping("/postloves")
    @Operation(summary = "게시물 내 하트 삭제", description = "게시물에 남긴 좋아요를 취소하는 API 입니다.")
    public BaseResponse<String> deletePostLove(@AuthenticationPrincipal @Parameter(hidden=true) User user,
                                               @Parameter(description = "좋아요를 취소할 게시글의 정보")
                                               @RequestBody PostLoveReq postLoveReq) {
        try {
            postLoveService.deleteLove(user, postLoveReq);

            return new BaseResponse<>("게시글 좋아요를 취소하셨습니다.");
        } catch (BaseException e){
            return new BaseResponse<>(FIND_FAIL_POSTLOVE);
        }
    }
}
