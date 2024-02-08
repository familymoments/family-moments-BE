package com.spring.familymoments.domain.socialInfo.feign.google;

import com.spring.familymoments.config.secret.FeignConfiguration;
import com.spring.familymoments.domain.socialInfo.model.GoogleLoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "googleUser", url = "https://www.googleapis.com", configuration = FeignConfiguration.class)
public interface GoogleUserApi {
    @GetMapping("/userinfo/v2/me")
    ResponseEntity<GoogleLoginResponse> getUserInfo(@RequestParam("access_token") String accessToken);
}
