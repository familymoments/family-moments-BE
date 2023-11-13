package com.spring.familymoments.domain.family.model;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FamilyUpdateDto {

    private String owner;

    @NotEmpty(message = "가족 이름을 입력해주세요.")
    private String familyName;
}
