package com.spring.familymoments.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "채팅방 프로필 Res")
@Builder
@Getter
public class ChatProfile {
    @Schema(description = "유저 id")
    String id;
    @Schema(description = "유저 닉네임")
    String nickname;
    @Schema(description = "프로필 이미지 링크")
    String profileImg;
}
