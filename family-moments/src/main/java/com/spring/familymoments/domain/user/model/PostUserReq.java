package com.spring.familymoments.domain.user.model;

import lombok.*;

@NoArgsConstructor
public class PostUserReq {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class joinUser {
        private String id;
        private String password;
        private String name;
        private String email;
        // private LocalDateTime birthDate;
        private String strBirthDate;
        private String nickname;
        private String profileImg;
    }

}