package com.spring.familymoments.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 찾기 단계 중 아이디 존재 여부 확인 관련 Request")
public class GetUserIdReq {
    @NotBlank(message = "아이디를 입력해주세요.")
    @Schema(description = "아이디", example = "family12")
    private String userId;
}
