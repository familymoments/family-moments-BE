package com.spring.familymoments.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor
@Schema(description = "회원 가입 관련 Response")
public class PostUserRes {

    @Schema(description = "이메일", example = "family@gmail.com")
    private String email;
    @Schema(description = "닉네임", example = "영희남편철수")
    private String nickname;
    @Schema(description = "프로필 이미지")
    private String profileImg;

    public PostUserRes(String email, String nickname, String profileImg) {
        this.email = email;
        this.nickname = nickname;
        this.profileImg = profileImg;
    }
}
