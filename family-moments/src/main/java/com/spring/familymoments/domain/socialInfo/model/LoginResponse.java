package com.spring.familymoments.domain.socialInfo.model;

import com.spring.familymoments.config.secret.jwt.model.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginResponse {
    private TokenDto token;
    private Long userId;
}
