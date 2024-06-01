package com.spring.familymoments.domain.socialInfo;

import com.spring.familymoments.domain.socialInfo.model.SocialAuthResponse;
import com.spring.familymoments.domain.socialInfo.model.SocialUserResponse;
import org.springframework.stereotype.Service;

@Service
public interface SocialLoginService {
    UserType getServiceName();
    SocialAuthResponse getAccessToken(String authorizationCode);
    SocialUserResponse getUserInfo(String accessToken);
}
