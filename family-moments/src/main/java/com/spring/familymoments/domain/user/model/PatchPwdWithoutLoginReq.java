package com.spring.familymoments.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PatchPwdWithoutLoginReq {
    // private String email;
    private String passwordA;
    private String passwordB;

}
