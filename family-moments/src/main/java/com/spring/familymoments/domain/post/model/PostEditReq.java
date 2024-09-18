package com.spring.familymoments.domain.post.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Schema(description = "게시물 수정 Request (새로운 이미지 포함)")
public class PostEditReq {
    @Schema(description = "가족 id")
    private Long familyId;
    @Schema(description = "바꾸지 않을(기존) 사진의 url 문자열 리스트 (','로 구분)", example = "[https://url.com/img1.png, https://url.com/img2.png]")
    private List<String> urls;
    @Schema(description = "새로 업로드 할 이미지", example = "[img1.png, img2.png]")
    private List<MultipartFile> newImgs;
    @Schema(description = "수정한 게시물의 본문", example = "오늘은 날씨가 좋아요")
    private String content;
}
