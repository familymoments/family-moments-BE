package com.spring.familymoments.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Schema(description = "회원 가입 관련 Request")
public class PostUserReq {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class joinUser {
        @NotBlank(message = "아이디를 입력해주세요.")
        @Schema(description = "아이디", example = "family12")
        private String id;
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Schema(description = "비밀번호", example = "family1212")
        private String password;
        @NotBlank(message = "이메일을 입력해주세요.")
        @Schema(description = "이메일", example = "family@gmail.com")
        private String email;
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Schema(description = "닉네임", example = "영희남편철수")
        private String nickname;
        @Schema(description = "프로필 이미지")
        private String profileImg;
    }

}