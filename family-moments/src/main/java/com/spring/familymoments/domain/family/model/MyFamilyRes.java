package com.spring.familymoments.domain.family.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "내 가족 리스트")
public class MyFamilyRes {
    @Schema(description = "가족 id")
    private Long familyId;
    @Schema(description = "가족 이름")
    private String familyName;
    @Schema(description = "대표 이미지")
    private String representImg;
}
