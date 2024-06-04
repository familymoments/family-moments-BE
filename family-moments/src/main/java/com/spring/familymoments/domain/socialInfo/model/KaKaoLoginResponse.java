package com.spring.familymoments.domain.socialInfo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KaKaoLoginResponse {
    private String id;
    @Builder.Default
    private KakaoLoginData kakao_account = KakaoLoginData.builder().build();
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoLoginData {
        private String name;
        @Builder.Default
        private KakaoProfile profile = KakaoProfile.builder().build();
        private String birthyear;
        private String birthday;
        private String email;

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class KakaoProfile {
            private String nickname;
            private String profile_image_url;
        }
    }

}
