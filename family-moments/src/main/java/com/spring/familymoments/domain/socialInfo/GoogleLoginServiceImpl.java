package com.spring.familymoments.domain.socialInfo;

import com.spring.familymoments.domain.socialInfo.feign.google.GoogleAuthApi;
import com.spring.familymoments.domain.socialInfo.feign.google.GoogleUserApi;
import com.spring.familymoments.domain.socialInfo.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("googleLoginService")
public class GoogleLoginServiceImpl implements SocialLoginService {
    private final GoogleAuthApi googleAuthApi;
    private final GoogleUserApi googleUserApi;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleAppKey;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleAppSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;
    @Value("${spring.security.oauth2.client.registration.google.authorization-grant-type}")
    private String googleGrantType;

    @Override
    public UserType getServiceName() {
        return UserType.GOOGLE;
    }

    @Override
    public SocialAuthResponse getAccessToken(String authorizationCode) {
        ResponseEntity<SocialAuthResponse> response = googleAuthApi.getAccessToken(
                GoogleRequestAccessTokenDto.builder()
                        .code(authorizationCode)
                        .client_id(googleAppKey)
                        .clientSecret(googleAppSecret)
                        .redirect_uri(googleRedirectUri)
                        .grant_type(googleGrantType)
                        .build()
        );

        log.info("google auth info {}", response.toString());

        return response.getBody();
    }

    @Override
    public SocialUserResponse getUserInfo(String accessToken) {
        ResponseEntity<GoogleLoginResponse> response = googleUserApi.getUserInfo(accessToken);
        log.info("google user response {}", response.toString());

        GoogleLoginResponse googleLoginResponse = response.getBody();

        return SocialUserResponse.builder()
                .name(googleLoginResponse.getName())
                .picture(googleLoginResponse.getPicture())
                .email(googleLoginResponse.getEmail())
                .build();
    }

    public String unlink(GoogleDeleteDto googleDeleteDto) {
        ResponseEntity<String> response = googleAuthApi.unlink(googleDeleteDto);
        log.info("google unlink response {}", response);
        return response.getBody();
    }
}
