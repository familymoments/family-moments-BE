package com.spring.familymoments.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 조회 관련 Request")
public class GetSearchUserRes {
    @Schema(description = "회원 아이디", example = "familya4")
    private String Id;
    @Schema(description = "회원 이미지", example = "https://familymoments-image-bucket.s3.ap-northeast-2.amazonaws.com")
    private String profileImg;
    @Schema(description = "유저 목록에서 활성화", example = "1")
    private int status;
}
