package com.spring.familymoments.domain.post.model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class PostReq{
    private Long familyId;
    private List<MultipartFile> imgs;
    private String content;
}