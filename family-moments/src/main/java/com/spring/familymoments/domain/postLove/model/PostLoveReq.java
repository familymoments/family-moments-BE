package com.spring.familymoments.domain.postLove.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLoveReq {

    private Long postId;

    public PostLoveReq(Long postId){
        this.postId = postId;
    }
}
