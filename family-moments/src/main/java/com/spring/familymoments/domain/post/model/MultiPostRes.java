package com.spring.familymoments.domain.post.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spring.familymoments.domain.common.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MultiPostRes {
    private Long postId;
    private String writer;
    private String profileImg;
    private String content;
    private List<String> imgs;
    private LocalDateTime createdAt;
    private Boolean loved;

    public MultiPostRes(Long postId, String writer, String profileImg, String content, String imgs, LocalDateTime createdAt, BaseEntity.Status status) {
        this.postId = postId;
        this.writer = writer;
        this.profileImg = profileImg;
        this.content = content;
        this.imgs = Arrays.asList(imgs.split(","));
        this.createdAt = createdAt;
        this.loved = status == BaseEntity.Status.ACTIVE;
    }
}
