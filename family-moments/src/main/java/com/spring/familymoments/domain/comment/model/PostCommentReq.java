package com.spring.familymoments.domain.comment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 생성 관련 Request")
public class PostCommentReq {
    @Schema(description = "댓글 내용", example = "와~~좋다~~")
    private String content;
}
