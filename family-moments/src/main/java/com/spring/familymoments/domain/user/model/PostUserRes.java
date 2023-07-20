package com.spring.familymoments.domain.user.model;

import lombok.*;

@Getter
@NoArgsConstructor
public class PostUserRes {

    private String email;
    private String nickname;
    private String profileImg;

    private String jwtToken;

    public PostUserRes(String email, String nickname, String profileImg, String jwtToken) {
        this.email = email;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.jwtToken = jwtToken;
    }

}
