package com.spring.familymoments.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "초대 리스트 확인 관련 Response")
public class GetInvitationRes {
    @Schema(description = "초대 요청을 보낸 가족의 이름")
    private String familyName;
    @Schema(description = "초대 요청을 보낸 회원의 닉네임")
    private String nickname;
    @Schema(description = "초대 요청을 보낸 회원의 프로필 이미지")
    private String profileImg;
}
