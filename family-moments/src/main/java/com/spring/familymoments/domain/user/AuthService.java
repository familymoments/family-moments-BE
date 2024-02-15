package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.secret.jwt.JwtService;
import com.spring.familymoments.config.secret.jwt.model.TokenDto;
import com.spring.familymoments.domain.common.UserFamilyRepository;
import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.redis.RedisService;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.PostLoginReq;
import com.spring.familymoments.domain.user.model.PostLoginRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static com.spring.familymoments.config.BaseResponseStatus.*;

/**
 * 요청 -> AT 검사 -> AT 유효 -> 요청 실행
 * 요청 -> AT 검사 -> AT 기간만 만료 -> AT, RT로 재발급 요청 -> RT 유효 -> 재발급
 * 요청 -> AT 검사 -> AT 기간만 만료 -> AT, RT로 재발급 요청 -> RT 유효X -> 재로그인
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserFamilyRepository userFamilyRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;
    private final String SERVER = "Server";

    /**
     * 로그인 : 인증 정보 저장 및 토큰 발급
     */
    public TokenDto login(PostLoginReq postLoginReq) {
        User user = userRepository.findById(postLoginReq.getId())
                .orElseThrow(() -> new BaseException(FAILED_TO_LOGIN_ID)); //아이디가 일치하지 않습니다.
        if(user.getStatus().equals(User.Status.INACTIVE)) {
            throw new BaseException(FAILED_TO_LOGIN); //탈퇴하거나 신고당한 유저입니다.
        }
        if(!passwordEncoder.matches(postLoginReq.getPassword(), user.getPassword())) {
            throw new BaseException(FAILED_TO_LOGIN_PWD); //비밀번호가 일치하지 않습니다.
        }

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(user.getUuid(), postLoginReq.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return generateToken(SERVER, authentication.getName());
    }
    /**
     * 로그인 시 familyId 전달
     */
    public PostLoginRes login_familyId(String id) {
        List<UserFamily> userFamilyList = userFamilyRepository.findFirstActiveUserFamilyByUserId(id, PageRequest.of(0,1));

        Long familyId = null;
        if(userFamilyList.size() != 0) {
            familyId = userFamilyList.get(0).getFamilyId().getFamilyId();
        }
        return new PostLoginRes(familyId);
    }
    /**
     * 재발급 필요 여부 확인
     * : Access Token이 만료일자만 초과한 유효한 토큰인지 검사
     *  true (-> 재발급)
     */
    public boolean validate(String requestAccessTokenInHeader) {
        return jwtService.validateAccessTokenOnlyExpired(requestAccessTokenInHeader);
    }

    /**
     * 토큰 재발급
     * : validate 메서드가 true 반환할 때만 사용
     * -> Access Token, Refresh Token 재발급
     */
    public TokenDto reissue(String requestAccessTokenInHeader, String requestRefreshToken) {
        Authentication authentication = jwtService.getAuthentication(requestAccessTokenInHeader);
        String principal = getPrincipal(requestAccessTokenInHeader);
        if(principal == null) {
            throw new BaseException(INVALID_USER_JWT);
        }

        String refreshTokenInRedis = redisService.getValues("RT(" + SERVER + "):" + principal);
        if(refreshTokenInRedis == null) { //Redis에 저장되어 있는 RefreshToken이 없을 경우
            return null; // 재로그인 요청
        }
        //요청된 RT의 유효성 검사 & Redis에 저장되어 있는 RT와 같은지 비교
        if(!jwtService.validateRefreshToken(requestRefreshToken) || !refreshTokenInRedis.equals(requestRefreshToken)) {
            redisService.deleteValues("RT(" + SERVER + "):" + principal); //탈취 가능성 때문에 삭제
            return null; // 재로그인 요청
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //토큰 재발급 및 Redis 업데이트
        redisService.deleteValues("RT(" + SERVER +"):" + principal);
        TokenDto tokenDto = jwtService.createToken(principal);
        saveRefreshToken(SERVER, principal, tokenDto.getRefreshToken());
        return tokenDto;
    }
    /**
     * 토큰 발급
     */
    public TokenDto generateToken(String provider, String uuid) {
        //refresh token이 이미 있을 경우
        if(redisService.getValues("RT(" + provider + "):" + uuid) != null) {
            redisService.deleteValues("RT(" + provider + "):" + uuid);
        }
        //AT, RT 생성 및 Redis에 RT 저장
        TokenDto tokenDto = jwtService.createToken(uuid);
        saveRefreshToken(provider, uuid, tokenDto.getRefreshToken());
        return tokenDto;
    }

    /**
     * RefreshToken을 Redis에 저장
     */
    @Transactional
    public void saveRefreshToken(String provider, String principal, String refreshToken) {
        redisService.setValuesWithTimeout("RT(" + provider + "):" + principal, //key
                refreshToken, //value
                jwtService.getTokenExpirationTime(refreshToken)); //timeout(millis)
    }

    /**
     * AccessToken으로부터 principal 추출
     */
    public String getPrincipal(String requestAccessToken) {
        return jwtService.getAuthentication(requestAccessToken).getName();
    }

    //토큰 앞에 토큰 종류 명시하는 것으로 변경되면, 사용해야 함.
    /*public String resolveToken(String requestAccessTokenInHeader) {
        return requestAccessTokenInHeader;
    }*/

    /**
     * 로그아웃 API
     * : Redis에 있는
     */
    @Transactional
    public void logout(String requestAccessTokenInHeader) {
        String principal = getPrincipal(requestAccessTokenInHeader);
        if(principal == null) {
            throw new BaseException(INVALID_USER_JWT);
        }
        //Redis에 저장되어 있는 RT 삭제
        String refreshTokenInRedis = redisService.getValues("RT(" + SERVER + "):" + principal);
        if(refreshTokenInRedis != null) {
            redisService.deleteValues("RT(" + SERVER + "):" + principal);
        }
        //Redis에 로그아웃 처리한 AT 저장
        long expiration = jwtService.getTokenExpirationTime(requestAccessTokenInHeader) - new Date().getTime();
        redisService.setValuesWithTimeout(requestAccessTokenInHeader, "logout", expiration);
    }

    /**
     * SocialToken(AT, RT)을 Redis에 저장 - 1
     *
     * Naver, Kakao, Google
     */
    @Transactional
    public void saveSocialToken(String provider, String principal, String token, Long timeout) {
        redisService.setValuesWithTimeout(provider + principal, //key
                token, //value
                timeout); //timeout(millis)
    }
}
