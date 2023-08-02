package com.spring.familymoments.domain.post.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
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
public class SinglePostRes {
    private Long postId;
    private String writer;
    private String profileImg;
    private String content;
    private List<String> imgs;
    private LocalDateTime createdAt;
    private int countLove;
    private Boolean loved;


    public SinglePostRes(Long postId, String writer, String profileImg, String content, String imgs, LocalDateTime createdAt, int countLove, BaseEntity.Status status) {
        this.postId = postId;
        this.writer = writer;
        this.profileImg = profileImg;
        this.content = content;
        this.imgs = Arrays.asList(imgs.split(","));
        this.createdAt = createdAt;
        this.countLove = countLove;
        this.loved = status == BaseEntity.Status.ACTIVE;
    }
}
