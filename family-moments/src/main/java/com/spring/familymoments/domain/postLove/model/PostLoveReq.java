package com.spring.familymoments.domain.postLove.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "게시글 좋아요 관련 Request")
public class PostLoveReq {

    @Schema(description = "좋아요를 누른 게시글의 ID", example = "1")
    private Long postId;

    public PostLoveReq(Long postId){
        this.postId = postId;
    }
}
