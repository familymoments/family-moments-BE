package com.spring.familymoments.domain.socialInfo.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserResponse {
    private Long userId;
    private String id;
    private String name;
    private String email;
    private String strBirthDate;
    private String nickname;
    private String profileImg;
}
