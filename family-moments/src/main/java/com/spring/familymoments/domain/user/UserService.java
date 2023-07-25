package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.secret.jwt.JwtService;
import com.spring.familymoments.domain.user.model.PostLoginReq;
import com.spring.familymoments.domain.user.model.PostLoginRes;
import com.spring.familymoments.domain.user.model.PostUserReq;
import com.spring.familymoments.domain.user.model.PostUserRes;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.utils.SHA256;
import com.spring.familymoments.utils.UuidUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.spring.familymoments.config.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    /**
     * createUser
     * [POST]
     * @return PostUserRes
     */
    // TODO: [중요] 로그인 API 구현 후 JWT Token 반환하는 부분 제거하기!
    @Transactional
    public PostUserRes createUser(PostUserReq.joinUser postUserReq, MultipartFile profileImage) throws BaseException {

        // TODO: 아이디 중복 체크
        Optional<User> checkUserId = userRepository.findById(postUserReq.getId());
        if(checkUserId.isPresent()){
            log.info("[createUser]: 이미 존재하는 아이디입니다!");
            throw new BaseException(POST_USERS_EXISTS_ID);
        }

        // TODO: 이메일 중복 체크
        Optional<User> checkUserEmail = userRepository.findByEmail(postUserReq.getEmail());
        if(checkUserEmail.isPresent()){
            log.info("[createUser]: 이미 존재하는 이메일입니다!");
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        // TODO: UUID 생성
        String uuid = UuidUtils.generateUUID();

        // TODO: 비밀번호 저장
        /*String encryptPwd;
        try {
            encryptPwd = SHA256.encrypt(postUserReq.getPassword());
            postUserReq.setPassword(encryptPwd);
        } catch (Exception exception) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }*/

        // TODO: BirthDate -> String에서 LocalDateTime으로 변환
        String strBirthDate = postUserReq.getStrBirthDate();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalDateTime parsedBirthDate = null;
        parsedBirthDate = LocalDate.parse(strBirthDate, dateTimeFormatter).atStartOfDay();

        User user = User.builder()
                .id(postUserReq.getId())
                .uuid(uuid)
                .email(postUserReq.getEmail())
                .password(passwordEncoder.encode(postUserReq.getPassword()))
                .name(postUserReq.getName())
                .nickname(postUserReq.getNickname())
                .birthDate(parsedBirthDate)
                .profileImg(postUserReq.getProfileImg())
                .status(User.Status.ACTIVE)
                .build();
        userRepository.save(user);

        return new PostUserRes(user.getEmail(), user.getNickname(), user.getProfileImg());
    }

    /**
     * 회원 정보를 받아오는 함수 -> 임시로 만든 함수라 로그인 구현 후 수정 필요!
     * [GET]
     * @return User 객체
     */
    public User getUser(String uuid) throws BaseException {
        return userRepository.findUserByUuid(uuid).orElseThrow(()-> new BaseException(FIND_FAIL_USERNAME));
    }

    /**
     * 로그인
     * [POST]
     * @return ok
     */
    public PostLoginRes createLogin(PostLoginReq postLoginReq, HttpServletResponse response) throws BaseException {
        // TODO: 로그인 아이디 확인 db의 Id랑 같은지 확인하고 토큰 돌려주기
        User user = userRepository.findById(postLoginReq.getId())
                .orElseThrow(() -> new BaseException(FIND_FAIL_USERNAME));
        if(!passwordEncoder.matches(postLoginReq.getPassword(), user.getPassword())) {
            throw new BaseException(FAILED_TO_LOGIN);
        }
        // TODO: 로그인 시 토큰 생성해서 header에 붙임.
        String token = jwtService.createToken(user.getUuid());
        response.setHeader("X-AUTH-TOKEN", token);

        // TODO: 클라이언트에 cookie로 토큰도 보냄
        Cookie cookie = new Cookie("X-AUTH-TOKEN", token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);

        return new PostLoginRes(postLoginReq.getId());
    }

    /**
     * 로그아웃
     * [POST]
     * @return ok
     */
    /*@PostMapping("/users/log-out")
    public void createLogout(HttpServletResponse response) {

    }*/
}
