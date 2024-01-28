package com.spring.familymoments.domain.socialInfo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "소셜 회원 등록하는 Request")
public class UserJoinRequest {
    @Schema(description = "소셜 서버 종류", example = "KAKAO")
    private String userType;
    @Schema(description = "아이디", example = "younghee1234")
    private String id;
    @Schema(description = "이름", example = "김영희")
    private String name;
    @Schema(description = "이메일", example = "younghee@kakao.com")
    private String email;
    @Schema(description = "생년월일", example = "20000101")
    private String strBirthDate;
    @Schema(description = "닉네임", example = "영희엄마")
    private String nickname;
    @Schema(description = "프로필 사진", example = "null(고정), 아래 profileImg에 등록 바람.")
    private String profileImg;
    @Schema(description = "소셜 고유 ID", example = "23231221421")
    private String snsId;
}
