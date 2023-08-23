package com.spring.familymoments.domain.family.model;

import com.spring.familymoments.domain.family.entity.Family;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FamilyIdDto {

    private Long familyId;
    private String owner;
    private String familyName;
    private int uploadCycle;
    private String inviteCode;
    private String representImg;

}
