package com.spring.familymoments.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "아이디 중복 확인 관련 Request")
public class GetDuplicateUserIdReq {
    @NotBlank(message = "아이디를 입력해주세요.")
    @Schema(description = "아이디", example = "family12")
    private String id;
}
