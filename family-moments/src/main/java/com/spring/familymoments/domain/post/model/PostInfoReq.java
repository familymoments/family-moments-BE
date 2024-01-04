package com.spring.familymoments.domain.post.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시물 생성 및 수정 Request")
public class PostInfoReq {
    @Schema(description = "게시물 본문", example = "오늘은 날씨가 좋아요")
    String content;
}
