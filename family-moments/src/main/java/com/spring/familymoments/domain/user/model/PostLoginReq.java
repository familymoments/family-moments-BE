package com.spring.familymoments.domain.user.model;

import com.spring.familymoments.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 관련 Request")
public class PostLoginReq {
    @NotBlank(message = "아이디를 입력하세요.")
    @Schema(description = "아이디", example = "family5")
    private String id;
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Schema(description = "비밀번호", example = "family1212")
    private String password;

    public User toEntity() {
        return User.builder()
                .id(this.id)
                .password(this.password)
                .build();
    }
}
