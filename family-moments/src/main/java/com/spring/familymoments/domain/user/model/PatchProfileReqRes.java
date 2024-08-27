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
@Schema(description = "회원 정보 수정 관련 Request, Response")
public class PatchProfileReqRes {
    @Schema(description = "회원 닉네임", example = "영희남편철수")
    private String nickname;
    @Schema(description = "회원 사진", example = "https://familymoments-image-bucket.s3.ap-northeast-2.amazonaws.com")
    private String profileImg;
}
