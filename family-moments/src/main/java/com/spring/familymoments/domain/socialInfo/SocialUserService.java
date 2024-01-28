package com.spring.familymoments.domain.socialInfo;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.secret.jwt.model.TokenDto;
import com.spring.familymoments.domain.redis.RedisService;
import com.spring.familymoments.domain.socialInfo.entity.SocialInfo;
import com.spring.familymoments.domain.socialInfo.model.*;
import com.spring.familymoments.domain.user.AuthService;
import com.spring.familymoments.domain.user.UserDetailsService;
import com.spring.familymoments.domain.user.UserRepository;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.utils.UuidUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import static com.spring.familymoments.config.BaseResponseStatus.*;
import static com.spring.familymoments.domain.common.BaseEntity.Status.INACTIVE;
import static com.spring.familymoments.domain.user.entity.User.Status.ACTIVE;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialUserService {
    private final List<SocialLoginService> loginServices;
    private final SocialUserRepository socialUserRepository;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final AuthService authService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final String SERVER = "Server";
    private final PasswordEncoder passwordEncoder;
    @Value("${spring.security.oauth2.client.info.password}")
    private String password;
    private final RedisService redisService;
    private final KakaoLoginServiceImpl kakaoLoginService;
    private final NaverLoginServiceImpl naverLoginService;
    private final GoogleLoginServiceImpl googleLoginService;

    @Transactional
    public SocialLoginOrJoinResponse doSocialLogin(SocialLoginRequest request) {
        UserType type = UserType.valueOf(request.getUserType());

        SocialLoginService loginService = getLoginService(type);
        //1. 인가코드로 액세스 토큰 요청
        SocialAuthResponse socialAuthResponse = loginService.getAccessToken(request.getCode());

        //2. 토큰으로 소셜 API 호출 : 액세스 토큰으로 사용자 정보 가져오기
        SocialUserResponse socialUserResponse = loginService.getUserInfo(socialAuthResponse.getAccess_token());
        User user = socialUserRepository.findUserByEmailAndUserType(socialUserResponse.getEmail(), type);

        //+ 로그인 시 - REDIS 에 Social AT, RT 저장
        saveSocialToken(socialAuthResponse, request.getUserType(), socialUserResponse.getSnsId());

        TokenDto tokenDto = null;
        String strBirthDate = null;
        if(user != null) {
            //3. 자체 로그인 처리 (회원가입 필요없음)
            tokenDto = setAuthenticationInSocial(user);
        }
        if(socialUserResponse.getBirthday() != null && socialUserResponse.getBirthyear() != null) {
            if(type.equals(UserType.NAVER)) {
                //naver birthdate : MM-dd
                String str = socialUserResponse.getBirthday().replace("-", "");
                StringBuilder sb = new StringBuilder();
                sb.append(socialUserResponse.getBirthyear());
                sb.append(str);
                strBirthDate = sb.toString();
            } else {
                //kakao birthdate : MMdd
                strBirthDate = socialUserResponse.getBirthyear() + socialUserResponse.getBirthday();
            }
        }

        SocialLoginOrJoinResponse.LoginResponse loginResponse = SocialLoginOrJoinResponse.LoginResponse.builder()
                .tokenDto(tokenDto)
                .build();

        SocialLoginOrJoinResponse.JoinResponse joinResponse = SocialLoginOrJoinResponse.JoinResponse.builder()
                .snsId(socialUserResponse.getSnsId())
                .name(socialUserResponse.getName())
                .email(socialUserResponse.getEmail())
                .strBirthDate(strBirthDate)
                .nickname(socialUserResponse.getNickname())
                .picture(socialUserResponse.getPicture())
                .build();

        return new SocialLoginOrJoinResponse(loginResponse, joinResponse);
    }
    private SocialLoginService getLoginService(UserType userType) {
        for(SocialLoginService loginService : loginServices) {
            if(userType.equals(loginService.getServiceName())) {
                log.info("login service name: {}", loginService.getServiceName());
                return loginService;
            }
        }
        return new LoginServiceImpl();
    }
    @Transactional
    public void saveSocialToken(SocialAuthResponse socialAuthResponse, String userType, String snsId) {
        Long atTimeOut = Long.parseLong(socialAuthResponse.getExpires_in()) * 1000L;
        log.info("atTimeOut {}", atTimeOut);

        //AT REDIS에 저장
        authService.saveSocialToken("AT("+userType+"):", snsId,
                socialAuthResponse.getAccess_token(),
                atTimeOut);

        Long rtTimeOut = null;
        if(userType.equals("KAKAO")) {
            //KAKAO RT REDIS에 저장
            rtTimeOut = Long.parseLong(socialAuthResponse.getRefresh_token_expires_in()) * 1000;
        } else if(userType.equals("NAVER")) {
            //NAVER RT REDIS에 저장 - 1년(고정)
            rtTimeOut = 31557600000L;
        } else {
            //GOOGLE RT REDIS에 저장 - 7일(고정)
            rtTimeOut = 86400000L;
        }
        log.info("rtTimeOut {}", rtTimeOut);

        authService.saveSocialToken("RT("+userType+"):", snsId,
                socialAuthResponse.getRefresh_token(),
                rtTimeOut);
    }
    @Transactional
    public TokenDto createSocialUser(UserJoinRequest userJoinRequest) {
        String email = userJoinRequest.getEmail();
        UserType type = UserType.valueOf(userJoinRequest.getUserType());

        User existedU = socialUserRepository.findUserByEmailAndUserType(email, type);
        if(existedU != null) {
            throw new BaseException(FAILED_SOCIAL_JOIN);
        }

        Long userId = joinUser(userJoinRequest);
        User user = userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_USER_ID));

        return setAuthenticationInSocial(user);
        /*
        회원가입 내용 반환 시
        String formatPattern = "yyyyMMdd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
        String strBirthDate = user.getBirthDate().format(formatter);
        return new UserJoinResponse(user.getId(), user.getName(), strBirthDate, user.getNickname(), user.getProfileImg());
        */
    }

    @Transactional
    public Long joinUser(UserJoinRequest userJoinRequest) {
        UserType userType = UserType.valueOf(userJoinRequest.getUserType());

        //uuid 생성
        String uuid = UuidUtils.generateUUID();

        //날짜 자료형 변환
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime parsedBirthDate = null;
        parsedBirthDate = LocalDate.parse(userJoinRequest.getStrBirthDate(), dateTimeFormatter).atStartOfDay();

        //user entity + socialInfo entity
        User user = userRepository.save(
                User.builder()
                        .id(userJoinRequest.getId())
                        .email(userJoinRequest.getEmail())
                        .password(passwordEncoder.encode(password))
                        .uuid(uuid)
                        .name(userJoinRequest.getName())
                        .nickname(userJoinRequest.getNickname())
                        .birthDate(parsedBirthDate)
                        .profileImg(userJoinRequest.getProfileImg())
                        .status(ACTIVE)
                        .build()
        );
        SocialInfo socialInfo = socialUserRepository.save(
                SocialInfo.builder()
                        .type(userType)
                        .user(user)
                        .snsUserId(userJoinRequest.getSnsId())
                        .build()
        );

        return socialInfo.getUser().getUserId();
    }

    @Transactional
    public TokenDto setAuthenticationInSocial(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUuid());
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), password, userDetails.getAuthorities());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authService.generateToken(SERVER, authentication.getName());
    }

    @Transactional
    public void deleteSocialUserWithRedisProcess(User user) {
        List<SocialInfo> socialInfos = socialUserRepository.findSocialInfoByUser(user);

        if(socialInfos.size() != 0) {
            for (SocialInfo socialInfo : socialInfos) {
                socialInfo.updateStatus(INACTIVE);

                UserType type = socialInfo.getType();
                String socialName = "NORMAL";
                if(type == UserType.KAKAO) {
                    socialName = "KAKAO";
                } else if (type == UserType.NAVER) {
                    socialName = "NAVER";
                } else if (type == UserType.GOOGLE) {
                    socialName = "GOOGLE";
                }
                String snsUserId = socialInfo.getSnsUserId();

                String accessTokenInRedis = redisService.getValues("AT(" + socialName + "):" + snsUserId);

                if(accessTokenInRedis == null) {
                    //AT 만료 : 해당 소셜 로그인으로 재로그인 요청
                    throw new BaseException(EXPIRED_AT_ERROR);
                } else {
                    //네이버, 카카오, 구글과의 연결 끊기
                    String result = "";
                    if(type == UserType.KAKAO) {
                        StringBuilder bearerToken = new StringBuilder();
                        bearerToken.append("Bearer ");
                        bearerToken.append(accessTokenInRedis);
                        result = kakaoLoginService.unlink(bearerToken.toString(), Long.parseLong(snsUserId));
                    } else if (type == UserType.NAVER) {
                        result = naverLoginService.unlink(accessTokenInRedis);
                    } else if (type == UserType.GOOGLE) {
                        result = googleLoginService.unlink(
                                GoogleDeleteDto.builder()
                                        .token(accessTokenInRedis).build()
                        );
                    }
                    log.info("result {}", result);
                }

                //탈퇴 시 AT, RT 전부 삭제
                redisService.deleteValues("AT(" + socialName + "):" + snsUserId);
                String refreshTokenInRedis = redisService.getValues("RT(" + socialName + "):" + snsUserId);
                if(refreshTokenInRedis != null) {
                    redisService.deleteValues("RT(" + socialName + "):" + snsUserId);
                }
            }
        }
    }
}
