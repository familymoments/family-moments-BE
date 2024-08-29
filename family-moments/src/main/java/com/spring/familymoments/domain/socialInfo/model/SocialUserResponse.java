package com.spring.familymoments.domain.socialInfo.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class SocialUserResponse {
    //리소스 서버에서 가져온 정보들
    private String email;
    private String nickname;
    private String picture;
}