package com.spring.familymoments.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetSearchUserRes {
    private String Id;
    private String profileImg;
    private int status;
}
