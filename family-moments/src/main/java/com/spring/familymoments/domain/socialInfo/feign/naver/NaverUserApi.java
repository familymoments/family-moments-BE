package com.spring.familymoments.domain.socialInfo.feign.naver;

import com.spring.familymoments.config.secret.FeignConfiguration;
import com.spring.familymoments.domain.socialInfo.model.NaverLoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "naverUser", url = "https://openapi.naver.com", configuration = FeignConfiguration.class)
public interface NaverUserApi {
    @GetMapping("/v1/nid/me")
    ResponseEntity<NaverLoginResponse> getUserInfo(@RequestHeader Map<String, String> header);
    @GetMapping("/v1/nid/me")
    ResponseEntity<String> unlink(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("access_token") String access_token,
            @RequestParam("grant_type") String grantType //delete
    );
}
