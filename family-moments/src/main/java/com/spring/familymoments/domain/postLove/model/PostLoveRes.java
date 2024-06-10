package com.spring.familymoments.domain.postLove.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "하트를 누른 사람 리스트 조회 Response")
public class PostLoveRes {
    @Schema(description = "하트를 누른 사람의 닉네임", example = "벤티")
    private String nickname;
    @Schema(description = "하트를 누른 사람의 프로필 이미지")
    private String profileImg;

    public PostLoveRes(String nickname, String profileImg) {
        this.nickname = nickname;
        this.profileImg = profileImg;
    }
}
