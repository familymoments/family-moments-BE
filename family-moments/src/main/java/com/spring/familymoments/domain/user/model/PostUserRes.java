package com.spring.familymoments.domain.user.model;

import lombok.*;

@Getter
@NoArgsConstructor
public class PostUserRes {

    private String email;
    private String nickname;
    private String profileImg;

    public PostUserRes(String email, String nickname, String profileImg) {
        this.email = email;
        this.nickname = nickname;
        this.profileImg = profileImg;
    }
}
