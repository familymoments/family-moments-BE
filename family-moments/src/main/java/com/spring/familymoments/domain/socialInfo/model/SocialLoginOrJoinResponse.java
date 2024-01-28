package com.spring.familymoments.domain.socialInfo.model;

import com.spring.familymoments.config.secret.jwt.model.TokenDto;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginOrJoinResponse {
    private LoginResponse loginResponse;
    private JoinResponse joinResponse;
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private TokenDto tokenDto;
    }
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinResponse {
        private String snsId;
        private String name;
        private String email;
        private String strBirthDate;
        private String nickname;
        private String picture;
    }
}
