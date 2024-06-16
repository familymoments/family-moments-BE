package com.spring.familymoments.domain.post.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.spring.familymoments.domain.common.BaseEntity;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Single post Response (with number of loved)")
public class SinglePostRes {
    @Schema(description = "postId", example = "12343")
    private Long postId;
    @Schema(description = "작성자", example = "민니")
    private String writer;
    @Schema(description = "작성자 프로필 이미지", example = "https://url.com/name.png")
    private String profileImg;
    @Schema(description = "게시글 본문", example = "좋은 하루~")
    private String content;
    @Schema(description = "게시글 내 사진 리스트", example = "[https://url.com/img.png, https://url.com/img1.png]")
    private List<String> imgs;
    @JsonIgnore
    private LocalDateTime createdAtLocalDateTime;
    @Schema(description = "게시글 생성일", example = "yyyy-MM-dd")
    private LocalDate createdAt;
    @Schema(description = "게시글의 좋아요 개수", example = "2")
    private int countLove;
    @Schema(description = "나의 좋아요 여부", example = "true or false")
    private Boolean loved;
    @Schema(description = "본인의 게시물 여부", example = "true or false")
    private Boolean written;

    public SinglePostRes(Long postId, String writer, String profileImg, String content, String imgs, LocalDateTime createdAt, int countLove, BaseEntity.Status status) {
        this.postId = postId;
        this.writer = writer;
        this.profileImg = profileImg;
        this.content = content;
        this.imgs = Arrays.asList(imgs.split(","));
        this.createdAt = createdAt.toLocalDate();
        this.countLove = countLove;
        this.loved = status == BaseEntity.Status.ACTIVE;
        this.written = false;
    }
}
