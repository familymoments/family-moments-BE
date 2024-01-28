package com.spring.familymoments.domain.socialInfo.feign.kakao;

import com.spring.familymoments.config.secret.FeignConfiguration;
import com.spring.familymoments.domain.socialInfo.model.KaKaoLoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(value = "kakoUser", url = "https://kapi.kakao.com", configuration = FeignConfiguration.class)
public interface KakaoUserApi {
    @GetMapping("/v2/user/me")
    ResponseEntity<KaKaoLoginResponse> getUserInfo(@RequestHeader Map<String, String> header);
}
