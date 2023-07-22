package com.spring.familymoments.domain.family.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spring.familymoments.domain.user.entity.User;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FamilyDto {
    //@JsonIgnore
    private String owner;
    private String familyName;
    private int uploadCycle;
    private String inviteCode;
    private String representImg;
}
