package com.spring.familymoments.domain.socialInfo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginRequest {
    //사용자가 권한 부여 서버에게 자격 증명을 같이 보내는 단계
    @NotNull
    private String userType;
    @NotNull
    private String code;
}
