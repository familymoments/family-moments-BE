package com.spring.familymoments.domain.socialInfo.model;

import com.spring.familymoments.config.secret.jwt.model.TokenDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "소셜 회원가입")
public class SocialJoinResponse {
    @Schema(description = "토큰", example = "eeeeeeeeee")
    private TokenDto tokenDto;

    @Schema(description = "가족 아이디", example = "1")
    private Long familyId;

    public static SocialJoinResponse of(TokenDto tokenDto, Long familyId) {
        return SocialJoinResponse.builder()
                .tokenDto(tokenDto)
                .familyId(familyId)
                .build();
    }
}
