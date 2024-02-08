package com.spring.familymoments.domain.socialInfo.feign.kakao;

import com.spring.familymoments.config.secret.FeignConfiguration;
import com.spring.familymoments.domain.socialInfo.model.SocialAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "kakaoAuth", url = "https://kauth.kakao.com", configuration = FeignConfiguration.class)
public interface KakaoAuthApi {
    @GetMapping("/oauth/token")
    ResponseEntity<SocialAuthResponse> getAccessToken(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("grant_type") String grantType,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("code") String authorizationCode
    );

}
