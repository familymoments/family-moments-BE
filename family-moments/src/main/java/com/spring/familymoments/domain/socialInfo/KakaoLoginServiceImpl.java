package com.spring.familymoments.domain.socialInfo;

import com.spring.familymoments.domain.socialInfo.feign.kakao.KakaoAuthApi;
import com.spring.familymoments.domain.socialInfo.feign.kakao.KakaoUserApi;
import com.spring.familymoments.domain.socialInfo.model.KaKaoDeleteDto;
import com.spring.familymoments.domain.socialInfo.model.KaKaoLoginResponse;
import com.spring.familymoments.domain.socialInfo.model.SocialAuthResponse;
import com.spring.familymoments.domain.socialInfo.model.SocialUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("kakaoLoginService")
public class KakaoLoginServiceImpl implements SocialLoginService {
    private final KakaoAuthApi kakaoAuthApi;
    private final KakaoUserApi kakaoUserApi;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoAppKey;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoAppSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String kakaoGrantType;

    @Override
    public UserType getServiceName() {
        return UserType.KAKAO;
    }

    /**
     * 이 부분은 안드로이드에서 진행
     */
    @Override
    public SocialAuthResponse getAccessToken(String authorizationCode) {
        ResponseEntity<SocialAuthResponse> response = kakaoAuthApi.getAccessToken(
                kakaoAppKey,
                kakaoAppSecret,
                kakaoGrantType,
                kakaoRedirectUri,
                authorizationCode
        );

        log.info("kakao auth response {}", response.toString());

        return response.getBody();
    }

    @Override
    public SocialUserResponse getUserInfo(String accessToken) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("authorization", "Bearer " + accessToken);

        ResponseEntity<KaKaoLoginResponse> response = kakaoUserApi.getUserInfo(headerMap);

        log.info("kakao user response {}", response.toString());

        KaKaoLoginResponse kaKaoLoginResponse = response.getBody();
        KaKaoLoginResponse.KakaoLoginData kakaoLoginData = kaKaoLoginResponse.getKakao_account();
        KaKaoLoginResponse.KakaoLoginData.KakaoProfile kakaoProfile = kakaoLoginData.getProfile();

        return SocialUserResponse.builder()
                .snsId(kaKaoLoginResponse.getId())
                .name(kakaoLoginData.getName())
                .email(kakaoLoginData.getEmail())
                .birthyear(kakaoLoginData.getBirthyear())
                .birthday(kakaoLoginData.getBirthday())
                .nickname(kakaoProfile.getNickname())
                .picture(kakaoProfile.getProfile_image_url())
                .build();
    }

    public String unlink(String accessToken, Long target_id) {
        ResponseEntity<String> response = kakaoUserApi.unlink(
                accessToken,
                KaKaoDeleteDto.builder()
                        .target_id_type("user_id")
                        .target_id(target_id)
                        .build()
        );

        log.info("kakao unlink response {}", response.toString());
        return response.getBody();
    }
}
