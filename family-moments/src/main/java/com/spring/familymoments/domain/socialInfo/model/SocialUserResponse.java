package com.spring.familymoments.domain.socialInfo.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class SocialUserResponse {
    //리소스 서버에서 가져온 정보들
    private String name;
    private String email;
    private String birthyear;
    private String birthday;
    private String nickname;
    private String picture;
}