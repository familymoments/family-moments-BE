package com.spring.familymoments.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
public class PostEmailReq {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class sendVerificationEmail {
        @NotBlank(message = "이름을 입력해주세요.")
        private String name;
        @NotBlank(message = "인증 코드를 받으실 이메일을 입력해주세요.")
        private String email;
        @NotBlank(message = "받은 인증 코드를 입력해주세요.")
        private String code;
    }
}
