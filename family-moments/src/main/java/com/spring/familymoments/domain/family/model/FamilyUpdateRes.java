package com.spring.familymoments.domain.family.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "가족 정보 수정 Request")
public class FamilyUpdateRes {

    @Schema(description = "가족 권한자")
    private String owner;

    @NotEmpty(message = "가족 이름을 입력해주세요.")
    private String familyName;
}
