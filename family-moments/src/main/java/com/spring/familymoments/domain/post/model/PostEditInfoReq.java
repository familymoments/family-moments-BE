package com.spring.familymoments.domain.post.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Schema(description = "게시물 수정 Request (새로운 이미지 미포함)")
public class PostEditInfoReq {
    @Schema(description = "수정한 게시물의 본문", example = "오늘은 날씨가 좋아요")
    private String content;
    @Schema(description = "바꾸지 않을(기존) 사진의 url 문자열 리스트 (','로 구분)", example = "[https://url.com/img1.png, https://url.com/img2.png]")
    private List<String> urls;
}
