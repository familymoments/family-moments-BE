package com.spring.familymoments.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetProfileRes {
    private String name;
    private String birthDate;
    private String profileImg;
    private String nickName;
    private String email;

    //게시물 개수
    private Long totalUpload;

    //앱에 가입한 날짜로부터 경과 시간
    private Long duration;
}
