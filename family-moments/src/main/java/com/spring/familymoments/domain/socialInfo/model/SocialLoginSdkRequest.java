package com.spring.familymoments.domain.socialInfo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "소셜 가입 여부 확인 Request")
public class SocialLoginSdkRequest {
    @Schema(description = "소셜 고유 ID", example = "23231221421")
    private String snsId;

    @Schema(description = "이메일", example = "younghee@naver.com")
    private String email;
}
