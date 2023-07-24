package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.secret.jwt.JwtService;
import com.spring.familymoments.domain.user.model.PostUserReq;
import com.spring.familymoments.domain.user.model.PostUserRes;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.utils.SHA256;
import com.spring.familymoments.utils.UuidUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * createUser
     * [POST]
     * @return PostUserRes
     */
    // TODO: [중요] 로그인 API 구현 후 JWT Token 반환하는 부분 제거하기!
    @Transactional
    public PostUserRes createUser(PostUserReq.joinUser postUserReq, MultipartFile profileImage) throws BaseException {

        // TODO: 아이디 중복 체크
        if(checkDuplicateId(postUserReq.getId())){
            log.info("[createUser]: 이미 존재하는 아이디입니다!");
            throw new BaseException(POST_USERS_EXISTS_ID);
        }

        // TODO: 이메일 중복 체크
        // Optional<User> checkUserEmail = userRepository.findByEmail(postUserReq.getEmail());
        if(checkDuplicateEmail(postUserReq.getEmail())){
            log.info("[createUser]: 이미 존재하는 이메일입니다!");
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        // TODO: UUID 생성
        String uuid = UuidUtils.generateUUID();

        // TODO: 비밀번호 저장
        String encryptPwd;
        try {
            encryptPwd = SHA256.encrypt(postUserReq.getPassword());
            postUserReq.setPassword(encryptPwd);
        } catch (Exception exception) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        // TODO: BirthDate -> String에서 LocalDateTime으로 변환
        String strBirthDate = postUserReq.getStrBirthDate();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalDateTime parsedBirthDate = null;
        parsedBirthDate = LocalDate.parse(strBirthDate, dateTimeFormatter).atStartOfDay();

        // User saveUser = userRepository.save(saveUser);

        User user = User.builder()
                .id(postUserReq.getId())
                .uuid(uuid)
                .email(postUserReq.getEmail())
                .password(encryptPwd)
                .name(postUserReq.getName())
                .nickname(postUserReq.getNickname())
                .birthDate(parsedBirthDate)
                .profileImg(postUserReq.getProfileImg())
                .status(User.Status.ACTIVE)
                .build();
        userRepository.save(user);

        String testToken = jwtService.createToken(user.getUuid());

        return new PostUserRes(user.getEmail(), user.getNickname(), user.getProfileImg(), testToken);
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
     * 아이디 중복 확인
     * [GET]
     * @return 이미 가입된 아이디면 -> true, 그렇지 않으면 -> false
     */
    public boolean checkDuplicateId(String UserId) throws BaseException {
        return userRepository.existsById(UserId);
    }

    /**
     * 이메일 중복 확인
     * [GET]
     * @return 이미 가입된 이메일이면 -> true, 그렇지 않으면 -> false
     */
    public boolean checkDuplicateEmail(String email) throws BaseException {
        return userRepository.existsByEmail(email);
    }
}
