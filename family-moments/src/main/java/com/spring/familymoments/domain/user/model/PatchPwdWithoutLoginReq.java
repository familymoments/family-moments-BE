package com.spring.familymoments.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 재설정(로그인을 하지 않은 상태) 관련 Request")
public class PatchPwdWithoutLoginReq {
    @Schema(description = "새 비밀번호", example = "family1212")
    private String passwordA;
    @Schema(description = "새 비밀번호 확인", example = "family1212")
    private String passwordB;

}
