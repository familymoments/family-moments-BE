package com.spring.familymoments.domain.socialInfo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginResponse {
    private String id;
    private String name;
    private String picture;
    //private String birthday; 구글의 다른 api로 정보 얻을 수 있음
    private String email;
}
