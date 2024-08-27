package com.spring.familymoments.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Schema(description = "본인 인증 메일 발송과 관련된 Request")
public class PostEmailReq {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class sendVerificationEmail {
        @NotBlank(message = "인증 코드를 받으실 이메일을 입력해주세요.")
        @Schema(description = "이메일", example = "family@gmail.com")
        private String email;
        @Schema(description = "인증 코드", example = "여섯 자리 숫자로 이루어진 임의의 인증 코드")
        private String code;
    }

}
