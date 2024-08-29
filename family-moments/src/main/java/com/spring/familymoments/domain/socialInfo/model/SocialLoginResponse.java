package com.spring.familymoments.domain.socialInfo.model;

import com.spring.familymoments.config.secret.jwt.model.TokenDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인(토큰을 발급)하거나 리소스 서버 정보들을 반환하는 Response")
public class SocialLoginResponse {
    @Schema(description = "기존회원있음", example = "true")
    private Boolean isExisted;
    @Schema(description = "이메일", example = "younghee@kakao.com")
    private String email;
    @Schema(description = "닉네임", example = "영희엄마")
    private String nickname;
    @Schema(description = "프로필 사진", example = "[url]")
    private String picture;
    @Schema(description = "가족 아이디", example = "null")
    private Long familyId;

    public static SocialLoginResponse of(Boolean isExisted, String email, String nickname,
                                         String picture, Long familyId) {

        return SocialLoginResponse.builder()
                .isExisted(isExisted)
                .email(email)
                .nickname(nickname)
                .picture(picture)
                .familyId(familyId)
                .build();
    }

}
