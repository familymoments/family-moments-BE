package com.spring.familymoments.domain.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetCommentsRes {

    private Long commentId;

    private String nickname;

    private String profileImg;

    private String content;

    private boolean heart;

    private LocalDateTime updatedAt;

}