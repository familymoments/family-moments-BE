package com.spring.familymoments.domain.user.model;

import com.spring.familymoments.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLoginReq {
    @NotBlank(message = "id를 입력하세요.")
    private String id;
    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password;

    public User toEntity() {
        return User.builder()
                .id(this.id)
                .password(this.password)
                .build();
    }
}
