package com.spring.familymoments.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUserRes {
    private String id;
    private String pw;

    //private String jwt;
    /*public PostUserRes(Long id) {
        this.id = id;
    }*/
}
