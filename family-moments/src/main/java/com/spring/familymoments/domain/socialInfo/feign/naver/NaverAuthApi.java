package com.spring.familymoments.domain.socialInfo.feign.naver;

import com.spring.familymoments.config.secret.FeignConfiguration;
import com.spring.familymoments.domain.socialInfo.model.SocialAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "naverAuth", url="https://nid.naver.com", configuration = FeignConfiguration.class)
public interface NaverAuthApi {
    @GetMapping("/oauth2.0/token")
    ResponseEntity<SocialAuthResponse> getAccessToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("code") String code,
            @RequestParam("state") String state
    );
}
