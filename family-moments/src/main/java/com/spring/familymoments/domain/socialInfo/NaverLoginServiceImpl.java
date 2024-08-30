package com.spring.familymoments.domain.socialInfo;

import com.spring.familymoments.domain.socialInfo.feign.naver.NaverAuthApi;
import com.spring.familymoments.domain.socialInfo.feign.naver.NaverUserApi;
import com.spring.familymoments.domain.socialInfo.model.NaverLoginResponse;
import com.spring.familymoments.domain.socialInfo.model.SocialAuthResponse;
import com.spring.familymoments.domain.socialInfo.model.SocialUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("naverLoginService")
public class NaverLoginServiceImpl implements SocialLoginService {
    private final NaverAuthApi naverAuthApi;
    private final NaverUserApi naverUserApi;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverAppKey;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverAppSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${spring.security.oauth2.client.registration.naver.authorization-grant-type}")
    private String naverGrantType;

    @Override
    public UserType getServiceName() {
        return UserType.NAVER;
    }

    @Override
    public SocialAuthResponse getAccessToken(String authorizationCode) {
        ResponseEntity<SocialAuthResponse> response = naverAuthApi.getAccessToken(
                naverGrantType,
                naverAppKey,
                naverAppSecret,
                authorizationCode,
                "state"
        );

        log.info("naver auth response {}", response.toString());

        return response.getBody();
    }

    public SocialUserResponse getUserInfo(String accessToken) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("authorization", "Bearer " + accessToken);

        ResponseEntity<NaverLoginResponse> response = naverUserApi.getUserInfo(headerMap);

        log.info("naver user response");
        log.info(response.toString());

        NaverLoginResponse naverLoginResponse = response.getBody();
        NaverLoginResponse.Response naverUserInfo = naverLoginResponse.getResponse();

        return SocialUserResponse.builder()
                .email(naverUserInfo.getEmail())
                .nickname(naverUserInfo.getNickname())
                .picture(naverUserInfo.getProfile_image())
                .build();
    }

    public String unlink(String accessToken) {
        ResponseEntity<String> response = naverUserApi.unlink(
                naverAppKey,
                naverAppSecret,
                accessToken,
                naverGrantType
        );

        log.info("naver unlink response {}", response.toString());
        return response.getBody();
    }
}
