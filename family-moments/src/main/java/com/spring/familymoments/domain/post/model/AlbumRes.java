package com.spring.familymoments.domain.post.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Album Response")
public class AlbumRes {
    @Schema(description = "post ID", example = "12312")
    private long postId;
    @Schema(description = "Post main img", example = "https://url.com/name.png")
    private String img1;
}
