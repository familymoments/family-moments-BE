package com.spring.familymoments.domain.socialInfo.feign.google;

import com.spring.familymoments.config.secret.FeignConfiguration;
import com.spring.familymoments.domain.socialInfo.model.GoogleRequestAccessTokenDto;
import com.spring.familymoments.domain.socialInfo.model.SocialAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "googleAuth", url = "https://oauth2.googleapis.com", configuration = FeignConfiguration.class)
public interface GoogleAuthApi {
    @PostMapping("/token")
    ResponseEntity<SocialAuthResponse> getAccessToken(@RequestBody GoogleRequestAccessTokenDto requestDto);
}
