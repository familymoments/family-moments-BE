package com.spring.familymoments.domain.family.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spring.familymoments.domain.family.entity.Family;
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

//    public Family toEntity() {
//        return Family.builder()
//                .owner()
//                .familyName(this.familyName)
//                .uploadCycle(this.uploadCycle)
//                .inviteCode(this.inviteCode)
//                .representImg(this.representImg)
//                .build();
//    }
}
