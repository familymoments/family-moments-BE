package com.spring.familymoments.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "댓글 좋아요 관련 Response")
public class CommentRes {
    // Comment 및 좋아요 유저 정보 위한 DTO
    @Schema(description = "댓글 작성자", example = "민니")
    String nickName;
    @Schema(description = "프로필 이미지")
    String profileImg;

    public CommentRes(String nickName, String profileImg) {
        this.nickName = nickName;
        this.profileImg = profileImg;
    }
}
