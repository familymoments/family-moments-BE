package com.spring.familymoments.domain.family.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetFamilyAllRes {
    private Long userId;
    private String nickname;
    private String profileImg;
}
