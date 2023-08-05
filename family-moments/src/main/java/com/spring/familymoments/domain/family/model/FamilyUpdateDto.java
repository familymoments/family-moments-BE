package com.spring.familymoments.domain.family.model;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FamilyUpdateDto {
    private String owner;
    private String familyName;
}
