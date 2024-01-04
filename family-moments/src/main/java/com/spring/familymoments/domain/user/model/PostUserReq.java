package com.spring.familymoments.domain.user.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
public class PostUserReq {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class joinUser {
        @NotBlank(message = "아이디를 입력해주세요.")
        private String id;
        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String passwordA;
        @NotBlank(message = "비밀번호를 한 번 더 입력해주세요.")
        private String passwordB;
        @NotBlank(message = "이름을 입력해주세요.")
        private String name;
        @NotBlank(message = "이메일을 입력해주세요.")
        private String email;
        // private LocalDateTime birthDate;
        @NotBlank(message = "생년월일을 입력해주세요.")
        private String strBirthDate;
        @NotBlank(message = "닉네임을 입력해주세요.")
        private String nickname;
        private String profileImg;
    }

}