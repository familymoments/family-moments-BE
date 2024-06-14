package com.spring.familymoments.domain.socialInfo.model;

import com.spring.familymoments.config.secret.jwt.model.TokenDto;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginDto {
    private Boolean isExisted;
    private TokenDto tokenDto;
    private String name;
    private String email;
    private String strBirthDate;
    private String nickname;
    private String picture;
    private Long familyId;

    public static SocialLoginDto of(Boolean isExisted, TokenDto tokenDto,
                                    String name, String email, String strBirthDate,
                                    String nickname, String picture,
                                    Long familyId) {

        return SocialLoginDto.builder()
                .isExisted(isExisted)
                .tokenDto(tokenDto)
                .name(name)
                .email(email)
                .strBirthDate(strBirthDate)
                .nickname(nickname)
                .picture(picture)
                .familyId(familyId)
                .build();
    }

}
