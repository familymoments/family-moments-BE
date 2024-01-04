package com.spring.familymoments.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이메일 중복 확인 관련 Request")
public class GetDuplicateUserEmailReq {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Schema(description = "이메일", example = "family@gmail.com")
    private String email;
}
