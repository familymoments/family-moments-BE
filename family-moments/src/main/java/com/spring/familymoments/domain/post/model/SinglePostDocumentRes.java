package com.spring.familymoments.domain.post.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Single post document Response (with content and images)")
public class SinglePostDocumentRes {
    @Schema(description = "postId(Single post Response의 postId와 동일)", example = "12343")
    private Long entityId;
    @Schema(description = "게시글 본문", example = "좋은 하루~")
    private String content;
    @Schema(description = "게시글 내 사진 리스트", example = "[https://url.com/img1.png, https://url.com/img2.png]")
    private List<String> urls;

    public SinglePostDocumentRes(Long entityId, String content, String urls) {
        this.entityId = entityId;
        this.content = content;
        this.urls = Arrays.asList(urls.split(","));
    }
}
