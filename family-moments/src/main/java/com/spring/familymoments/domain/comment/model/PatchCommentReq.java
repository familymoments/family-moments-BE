package com.spring.familymoments.domain.comment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 수정 관련 Request")
public class PatchCommentReq {
    @Schema(description = "댓글 내용", example = "댓글을 수정하려고 합니다~")
    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;
}
