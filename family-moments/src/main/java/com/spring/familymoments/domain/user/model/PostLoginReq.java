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
    @NotBlank(message = "id를 입력하세요.")
    @Schema(description = "아이디", example = "familya5")
    private String id;
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Schema(description = "비밀번호", example = "family1212")
    private String password;

    @NotBlank(message = "FCM 토큰을 전송해주세요.")
    @Schema(description = "FCM 토큰", example = "ADFIAPFJWPKJ132983J2LKM32FJ0DFJ0A9")
    private String fcmToken;

    public User toEntity() {
        return User.builder()
                .id(this.id)
                .password(this.password)
                .build();
    }
}
