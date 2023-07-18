package com.spring.familymoments.domain.user.model;

import com.spring.familymoments.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUserReq {
    private String id;
    private String password;
    private String name;
    private String email;
    private String birthDate;
    private String nickname;
    private String profileImg;

    public User toEntity() {
        return User.builder()
                .id(this.id)
                .password(this.password)
                .name(this.name)
                .email(this.email)
                .birthDate(this.birthDate)
                .nickname(this.nickname)
                .profileImg(this.profileImg)
                .build();
    }
}