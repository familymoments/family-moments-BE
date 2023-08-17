package com.spring.familymoments.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchPwdReq {
    private String password;
    private String newPassword_first;
    private String newPassword;
}
