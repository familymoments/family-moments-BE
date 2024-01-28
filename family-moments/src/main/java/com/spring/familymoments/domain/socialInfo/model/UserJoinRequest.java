package com.spring.familymoments.domain.socialInfo.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserJoinRequest {
    private String userType;
    private String id;
    private String name;
    private String email;
    private String strBirthDate;
    private String nickname;
    private String profileImg;
    private String snsId;
}
