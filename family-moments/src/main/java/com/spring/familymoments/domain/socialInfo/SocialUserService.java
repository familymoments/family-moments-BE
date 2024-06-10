package com.spring.familymoments.domain.socialInfo;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.secret.jwt.model.TokenDto;
import com.spring.familymoments.domain.awsS3.AwsS3Service;
import com.spring.familymoments.domain.fcm.FCMService;
import com.spring.familymoments.domain.socialInfo.entity.SocialInfo;
import com.spring.familymoments.domain.socialInfo.model.*;
import com.spring.familymoments.domain.user.AuthService;
import com.spring.familymoments.domain.user.UserDetailsService;
import com.spring.familymoments.domain.user.UserRepository;
import com.spring.familymoments.domain.user.UserService;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.PostLoginRes;
import com.spring.familymoments.utils.UuidUtils;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.spring.familymoments.config.BaseResponseStatus.*;
import static com.spring.familymoments.domain.common.BaseEntity.Status.INACTIVE;
import static com.spring.familymoments.domain.user.entity.User.Status.ACTIVE;
import static com.spring.familymoments.utils.ValidationRegex.*;

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
    private final UserService userService;
    private final AwsS3Service awsS3Service;
    private final FCMService fcmService;
    private final String SERVER = "Server";
    private final PasswordEncoder passwordEncoder;
    @Value("${spring.security.oauth2.client.info.password}")
    private String password;

    /**
     * 카카오/네이버/구글 메서드 분리
     */
    private SocialLoginService getLoginService(UserType userType) {
        for(SocialLoginService loginService : loginServices) {
            if(userType.equals(loginService.getServiceName())) {
                log.info("login service name: {}", loginService.getServiceName());
                return loginService;
            }
        }
        return new LoginServiceImpl();
    }

    /**
     * 소셜 로그인 API
     * @param socialToken
     * @param socialLoginSdkRequest
     * @return SocialLoginResponse(Token 정보/유저정보)
     */
    @Transactional
    public SocialLoginDto createSocialSdkUser(String socialToken, String fcmToken, SocialLoginSdkRequest socialLoginSdkRequest) {
        try {
            boolean isExisted = false;
            //userType
            String strUserType = socialLoginSdkRequest.getUserType();
            UserType enumUserType = UserType.getEnumUserTypeFromStringUserType(strUserType);
            //social-token으로 email 받아오기 (이메일 필수로 설정 -> 안드에서 세팅)
            SocialLoginService loginService = getLoginService(enumUserType);
            //소셜 회원의 유저 정보 받아오기
            SocialUserResponse socialUserResponse = loginService.getUserInfo(socialToken);
            //기존회원여부
            Optional<User> existedU = socialUserRepository.findUserByEmailAndUserType(socialUserResponse.getEmail(), enumUserType);

            TokenDto tokenDto = null;
            String strBirthDate = null;

            //기존회원 토큰발급 (회원가입 필요없음)
            if(existedU != null && existedU.isPresent()) {
                isExisted = true;
                tokenDto = setAuthenticationInSocial(existedU.get());
            }

            //신규회원의 유저정보 중 Naver와 Kakao의 bithday + birthyear -> birthdate 형식 반영
            if(socialUserResponse.getBirthday() != null && socialUserResponse.getBirthyear() != null) {
                if(enumUserType.equals(UserType.NAVER)) {
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

            // FCM Token 저장
            if (fcmToken == null || fcmToken.isEmpty()) {
                throw new BaseException(FIND_FAIL_FCMTOKEN);
            }
            fcmService.saveToken(socialUserResponse.getEmail(), fcmToken);

            return SocialLoginDto.of(
                    isExisted,
                    tokenDto,
                    socialUserResponse.getName(),
                    socialUserResponse.getEmail(),
                    strBirthDate,
                    socialUserResponse.getNickname(),
                    socialUserResponse.getPicture()
            );

        } catch (FeignException e) {
            throw new BaseException(INVALID_SOCIAL_TOKEN);
        }
    }


    /**
     * 소셜 회원 가입 API (소셜 서버에서 얻은 유저 정보 활용)
     * @param userJoinRequest
     * @return Tokendto
     */
    @Transactional
    public SocialJoinDto createSocialUser(UserJoinRequest userJoinRequest, MultipartFile profileImage) {
        //아이디
        if (userJoinRequest.getId().isEmpty()) {
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if (!isRegexId(userJoinRequest.getId())) {
            throw new BaseException(POST_USERS_INVALID_ID);
        }
        //아이디 중복 체크
        if (userService.checkDuplicateId(userJoinRequest.getId())) {
            throw new BaseException(POST_USERS_EXISTS_ID);
        }
        //이름
        if (userJoinRequest.getName().isEmpty()) {
            throw new BaseException(POST_USERS_EMPTY_NAME);
        }
        //생년월일
        if (userJoinRequest.getStrBirthDate().isEmpty()) {
            throw new BaseException(POST_USERS_EMPTY_BIRTH);
        }
        if (!isRegexBirth(userJoinRequest.getStrBirthDate())) {
            throw new BaseException(POST_USERS_INVALID_BIRTH);
        }
        //닉네임
        if (userJoinRequest.getNickname().isEmpty()) {
            throw new BaseException(POST_USERS_EMPTY_NICKNAME);
        }
        if (!isRegexNickName(userJoinRequest.getNickname())) {
            throw new BaseException(POST_USERS_INVALID_NICKNAME);
        }
        //프로필 사진
        String fileUrl = null;
        if (userJoinRequest.getProfileImg() == null) {
            fileUrl = awsS3Service.uploadImage(profileImage);
        }
        userJoinRequest.setProfileImg(fileUrl);

        //email
        String email = userJoinRequest.getEmail();
        //userType
        UserType enumUserType = UserType.getEnumUserTypeFromStringUserType(userJoinRequest.getUserType());
        Optional<User> existedU = socialUserRepository.findUserByEmailAndUserType(email, enumUserType);

        Long userId = joinUser(userJoinRequest);
        User user = userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_USER_ID));

        //AT, RT token
        TokenDto tokenDto = setAuthenticationInSocial(user);
        //familyId
        PostLoginRes postLoginRes = authService.login_familyId(userJoinRequest.getId());

        return SocialJoinDto.of(
                tokenDto,
                postLoginRes.getFamilyId()
        );
    }

    @Transactional
    public Long joinUser(UserJoinRequest userJoinRequest) {
        //userType
        UserType enumUserType = UserType.getEnumUserTypeFromStringUserType(userJoinRequest.getUserType());

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
                        .type(enumUserType)
                        .user(user)
                        .build()
        );

        return socialInfo.getUser().getUserId();
    }
    /**
     * 소셜 회원 탈퇴
     */
    @Transactional
    public void deleteSocialUserWithRedisProcess(User user) {
        List<SocialInfo> socialInfos = socialUserRepository.findSocialInfoByUser(user);

        if(!socialInfos.isEmpty()) {
            for (SocialInfo socialInfo : socialInfos) {
                socialInfo.updateStatus(INACTIVE);

                /*UserType type = socialInfo.getType();
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
                }*/
            }
        }
    }

    /**
     * 서비스 AT, RT 토큰 발급
     */
    @Transactional
    public TokenDto setAuthenticationInSocial(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUuid());
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), password, userDetails.getAuthorities());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authService.generateToken(SERVER, authentication.getName());
    }

}
