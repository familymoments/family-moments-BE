package com.spring.familymoments.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 조회 관련 Request")
public class GetSearchUserRes {
    @Schema(description = "회원 아이디", example = "familya4")
    private String id;
    @Schema(description = "회원 이미지", example = "https://familymoments-image-bucket.s3.ap-northeast-2.amazonaws.com")
    private String profileImg;
    @Schema(description = "유저 목록에서 활성화", example = "1")
    private int status;

    public static GetSearchUserRes of(String id, String profileImg, int status) {
        return GetSearchUserRes.builder()
                .id(id)
                .profileImg(profileImg)
                .status(status)
                .build();
    }
}
