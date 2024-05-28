package com.spring.familymoments.domain.socialInfo.model;

import com.spring.familymoments.config.secret.jwt.model.TokenDto;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialJoinDto {
    private TokenDto tokenDto;
    private Long familyId;

    public static SocialJoinDto of(TokenDto tokenDto, Long familyId) {
        return SocialJoinDto.builder()
                .tokenDto(tokenDto)
                .familyId(familyId)
                .build();
    }
}
