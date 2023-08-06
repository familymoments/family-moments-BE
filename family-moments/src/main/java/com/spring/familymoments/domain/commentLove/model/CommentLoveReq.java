package com.spring.familymoments.domain.commentLove.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLoveReq {
    private Long commentId;
}
