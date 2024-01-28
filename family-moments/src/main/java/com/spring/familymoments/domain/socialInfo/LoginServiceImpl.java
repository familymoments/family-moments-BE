package com.spring.familymoments.domain.socialInfo;

import com.spring.familymoments.domain.socialInfo.model.SocialAuthResponse;
import com.spring.familymoments.domain.socialInfo.model.SocialUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Component
@Qualifier("defaultLoginService")
public class LoginServiceImpl implements SocialLoginService {
    @Override
    public UserType getServiceName() {
        return UserType.NORMAL;
    }
    @Override
    public SocialAuthResponse getAccessToken(String authorizationCode) {
        return null;
    }
    @Override
    public SocialUserResponse getUserInfo(String accessToken) {
        return null;
    }
}
