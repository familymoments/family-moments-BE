package com.spring.familymoments.domain.post.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumRes {
    private long postId;
    private String img1;
}
