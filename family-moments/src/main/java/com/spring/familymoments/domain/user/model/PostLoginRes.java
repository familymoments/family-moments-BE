package com.spring.familymoments.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 관련 Response")
public class PostLoginRes {
    @Schema(description = "가족 아이디", example = "1")
    private Long familyId;

    public static PostLoginRes of(Long familyId) {
        return PostLoginRes.builder()
                .familyId(familyId)
                .build();
    }
}
