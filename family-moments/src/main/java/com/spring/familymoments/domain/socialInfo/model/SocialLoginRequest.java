package com.spring.familymoments.domain.socialInfo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "소셜 서버(권한 부여 서버)에 인가코드(자격 증명)를 보내는 Request")
public class SocialLoginRequest {
    @Schema(description = "소셜 서버 종류", example = "KAKAO")
    @NotNull
    private String userType;
    @Schema(description = "인가 코드", example = "yhgGywjpJXX8NaQJrgjN401U1VfW5__")
    @NotNull
    private String code;
}