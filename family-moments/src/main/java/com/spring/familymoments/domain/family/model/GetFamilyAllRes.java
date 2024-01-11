package com.spring.familymoments.domain.family.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "가족 전체 조회 관련 Response")
public class GetFamilyAllRes {
    @Schema(description = "유저 인덱스" , example = "1")
    private Long userId;

    @Schema(description = "닉네임" , example = "몰리")
    private String nickname;

    @Schema(description = "프로필 이미지" , example = "http://dfadfadf.png")
    private String profileImg;
}
