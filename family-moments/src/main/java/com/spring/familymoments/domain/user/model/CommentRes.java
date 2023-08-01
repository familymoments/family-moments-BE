package com.spring.familymoments.domain.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRes {
    // Comment 및 좋아요 유저 정보 위한 DTO
    String nickName;
    String profileImg;

    public CommentRes(String nickName, String profileImg) {
        this.nickName = nickName;
        this.profileImg = profileImg;
    }
}
