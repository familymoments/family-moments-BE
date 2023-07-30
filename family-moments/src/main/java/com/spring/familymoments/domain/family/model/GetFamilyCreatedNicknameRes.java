package com.spring.familymoments.domain.family.model;

import com.spring.familymoments.domain.family.entity.Family;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetFamilyCreatedNicknameRes {
    private String nickname;
    private String dday;

}
