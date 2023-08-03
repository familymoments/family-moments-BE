package com.spring.familymoments.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDuplicateUserIdReq {
    @NotBlank(message = "아이디를 입력해주세요.")
    private String id;
}
