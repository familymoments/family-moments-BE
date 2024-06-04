package com.spring.familymoments.domain.socialInfo;

import com.spring.familymoments.config.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.spring.familymoments.config.BaseResponseStatus.INVALID_USER_TYPE;

@RequiredArgsConstructor
@Getter
public enum UserType {
    KAKAO("KAKAO"),
    NAVER("NAVER"),
    GOOGLE("GOOGLE"),
    NORMAL("NORMAL");

    private final String stringUserType;

    public static UserType getEnumUserTypeFromStringUserType(String stringUserType) {
        return Arrays.stream(values())
                .filter(userType -> userType.getStringUserType().equals(stringUserType))
                .findFirst()
                .orElseThrow(() -> new BaseException(INVALID_USER_TYPE));
    }
}