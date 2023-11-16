package com.spring.familymoments.domain.comment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 조회 Response")
public class GetCommentsRes {
    @Schema(description = "게시글 인덱스" , example = "2")
    private Long postId;

    @Schema(description = "댓글 인덱스" , example = "3")
    private Long commentId;

    @Schema(description = "유저 닉네임" , example = "몰리")
    private String nickname;

    @Schema(description = "프로필 이미지" , example = "http://dfadfa.png")
    private String profileImg;

    @Schema(description = "댓글 내용" , example = "와~~ 좋다~~")
    private String content;

    @Schema(description = "좋아요" , example = "false")
    private boolean heart;

    @Schema(description = "생성 시각" , example = "2023-08-01T00:47:39")
    private LocalDateTime createdAt;

}
