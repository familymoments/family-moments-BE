package com.spring.familymoments.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "아이디 찾기, 본인 인증 메일 발송과 관련된 Response")
public class GetUserIdRes {
    @Schema(description = "아이디", example = "family12")
    private String userId;
}
