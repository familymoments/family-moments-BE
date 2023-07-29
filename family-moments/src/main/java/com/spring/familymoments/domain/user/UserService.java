package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.advice.exception.InternalServerErrorException;
import com.spring.familymoments.config.secret.jwt.JwtService;
import com.spring.familymoments.domain.comment.CommentWithUserRepository;
import com.spring.familymoments.domain.comment.entity.Comment;
import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.family.FamilyRepository;
import com.spring.familymoments.domain.post.PostWithUserRepository;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.user.model.*;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.utils.UuidUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.spring.familymoments.config.BaseResponseStatus.*;
import static com.spring.familymoments.domain.common.entity.UserFamily.Status.ACTIVE;
import static com.spring.familymoments.domain.common.entity.UserFamily.Status.DEACCEPT;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostWithUserRepository postWithUserRepository;
    private final FamilyRepository familyRepository;
    private final CommentWithUserRepository commentWithUserRepository;
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

    /**
     * 로그인
     * [POST]
     * @return ok
     */
    public PostLoginRes createLogin(PostLoginReq postLoginReq, HttpServletResponse response) throws InternalServerErrorException {
        // TODO: 로그인 아이디 확인 db의 Id랑 같은지 확인하고 토큰 돌려주기
        User user = userRepository.findById(postLoginReq.getId())
                .orElseThrow(() -> new InternalServerErrorException("아이디가 일치하지 않습니다."));
        if(!passwordEncoder.matches(postLoginReq.getPassword(), user.getPassword())) {
            throw new InternalServerErrorException("비밀번호가 일치하지 않습니다.");
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
     *
     * controller 부분에만 작성함.
     */

    /**
     * 회원정보 조회 API
     * [GET]
     * @return
     */
    public GetProfileRes readProfile(User user) {
        Long totalUpload = postWithUserRepository.countByWriterId(user);

        LocalDateTime targetDate = user.getCreatedAt();
        LocalDateTime currentDate = LocalDateTime.now();
        Long duration = ChronoUnit.DAYS.between(targetDate, currentDate);

        return new GetProfileRes(user.getProfileImg(), user.getNickname(), user.getEmail(), totalUpload, duration);
    }
    /**
     * 유저 검색 API
     * [GET] /users
     * @return
     */
    public List<GetSearchUserRes> searchUserById(String keyword, Long familyId, User loginUser) {
        List<GetSearchUserRes> getSearchUserResList = new ArrayList<>();

        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<User> keywordUserList = userRepository.findTop5ByIdContainingKeywordOrderByIdAsc(keyword, pageRequest);

        for(User keywordUser: keywordUserList) {
            int appear = 1;
            if(loginUser.getUserId() == keywordUser.getUserId()) {
                log.info("[로그인 유저이면 리스트에 추가 X]");
                continue;
            }
            List<Object[]> results = userRepository.findUsersByFamilyIdAndUserId(familyId, keywordUser.getUserId());
            for(Object[] result : results) {
                UserFamily userFamily = (UserFamily) result[1];
                if (userFamily == null) {
                    System.out.println("UserFamily is null. Skipping...");
                    continue;
                }
                if(userFamily.getStatus() == ACTIVE || userFamily.getStatus() == DEACCEPT) {
                    log.info("[이미 다른 가족에 초대 대기 중이거나 초대 당한 사람이니까 비활성화]");
                    appear = 0;
                    break;
                }
            }
            GetSearchUserRes getSearchUserRes = new GetSearchUserRes(keywordUser.getId(), keywordUser.getProfileImg(), appear);
            getSearchUserResList.add(getSearchUserRes);
        }
        return getSearchUserResList;
    }
    /**
     * 회원 정보 수정 API
     * [PATCH]
     * @return
     */
    public PatchProfileReqRes updateProfile(PatchProfileReqRes patchProfileReqRes, User user) {
        user.updateProfile(patchProfileReqRes);
        User updatedUser = userRepository.save(user);

        String formatPattern = "yyyyMMdd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
        String updateUserBirth = updatedUser.getBirthDate().format(formatter);

        return new PatchProfileReqRes(updatedUser.getName(), updatedUser.getNickname(), updateUserBirth, updatedUser.getProfileImg());
    }
    /**
     * 비밀번호 인증 API
     * [GET]
     * @return true || false
     */
    public boolean authenticate(GetPwdReq getPwdReq, User user) {
        if(getPwdReq.getPassword() == null || getPwdReq.getPassword() == "") {
            throw new NoSuchElementException("비밀번호를 입력하세요.");
        }
        return passwordEncoder.matches(getPwdReq.getPassword(), user.getPassword());
    }
    /**
     * 비밀번호 변경(마이페이지) API
     * [PATCH]
     * @return
     */
    public void updatePassword(PatchPwdReq patchPwdReq, User user) {
        user.updatePassword(passwordEncoder.encode(patchPwdReq.getNewPassword()));
        userRepository.save(user);
    }
    /**
     * 전체 회원정보 조회 API / 화면 외 API
     * [GET]
     * @return
     */
    public List<User> getAllUser() {
        List<User> userList = userRepository.findAll();
        return userList;
    }

    /**
     * 회원 탈퇴 API
     * [DELETE] /users
     * @return
     */
    @Transactional
    public void deleteUser(Long userId) {
        //1. 로그인 유저의 댓글 일괄 삭제
        List<Comment> comments = commentWithUserRepository.findCommentsByUserId(userId);
        if(comments != null) {
            commentWithUserRepository.deleteAll(comments);
        }
        //2. 로그인 유저의 게시글 일괄 삭제
        List<Post> posts = postWithUserRepository.findPostByUserId(userId);
        if(posts != null) {
            postWithUserRepository.deleteAll(posts);
        }
        //3. 로그인 유저 삭제
        userRepository.deleteById(userId);
    }
}

