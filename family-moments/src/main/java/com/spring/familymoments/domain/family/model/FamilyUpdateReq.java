package com.spring.familymoments.domain.family.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "가족 정보 수정 Request")
public class FamilyUpdateReq {

    @Size(max = 20, message = "가족 이름은 20자 이하입니다")
    @NotEmpty(message = "가족 이름을 입력해주세요.")
    private String familyName;
}
