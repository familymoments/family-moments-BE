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
@Schema(description = "비밀번호 수정 관련 Request")
public class PatchPwdReq {
    @Schema(description = "비밀번호", example = "family1212")
    private String password;
    @Schema(description = "새 비밀번호", example = "family1111")
    private String newPassword_first;
    @Schema(description = "새 비밀번호 확인", example = "family1111")
    private String newPassword;
}
