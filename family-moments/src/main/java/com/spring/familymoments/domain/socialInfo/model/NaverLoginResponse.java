package com.spring.familymoments.domain.socialInfo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NaverLoginResponse {
    private Response response = Response.builder().build();
    private String resultCode;
    private String message;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String nickname;
        private String email;
        private String profile_image;
    }

}
