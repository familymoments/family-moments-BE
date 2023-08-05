package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.advice.exception.InternalServerErrorException;
import com.spring.familymoments.config.secret.jwt.JwtService;
import com.spring.familymoments.domain.comment.CommentWithUserRepository;
import com.spring.familymoments.domain.comment.entity.Comment;
import com.spring.familymoments.domain.commentLove.CommentLoveWithUserRepository;
import com.spring.familymoments.domain.commentLove.entity.CommentLove;
import com.spring.familymoments.domain.common.UserFamilyRepository;
import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.family.FamilyRepository;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.post.PostWithUserRepository;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.postLove.PostLoveRepository;
import com.spring.familymoments.domain.postLove.entity.PostLove;
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
    private final UserFamilyRepository userFamilyRepository;
    private final CommentLoveWithUserRepository commentLoveWithUserRepository;
    private final PostLoveRepository postLoveRepository;
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
    public PostLoginRes createLogin(PostLoginReq postLoginReq, HttpServletResponse response) {
        // 로그인 아이디 확인 db의 Id랑 같은지 확인하고 토큰 돌려주기
        User user = userRepository.findById(postLoginReq.getId())
                .orElseThrow(() -> new NoSuchElementException("아이디가 일치하지 않습니다."));
        if(!passwordEncoder.matches(postLoginReq.getPassword(), user.getPassword())) {
            throw new NoSuchElementException("비밀번호가 일치하지 않습니다.");
        }
        // 로그인 시 토큰 생성해서 header에 붙이기
        String token = jwtService.createToken(user.getUuid());
        response.setHeader("X-AUTH-TOKEN", token);

        // 클라이언트에 cookie로 토큰도 보내기
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
    public GetProfileRes readProfile(User user, Long familyId) {
        Long totalUpload = new Long(0);
        if(familyId != null) {
            Family family = familyRepository.findById(familyId)
                    .orElseThrow(() -> new NoSuchElementException("현재 가족정보를 불러오지 못했습니다."));
            totalUpload = postWithUserRepository.countByWriterAndFamilyId(user, family);
        }
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
    @Transactional
    public List<GetSearchUserRes> searchUserById(String keyword, Long familyId, User loginUser) {
        List<GetSearchUserRes> getSearchUserResList = new ArrayList<>();

        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<User> keywordUserList = userRepository.findTop5ByIdContainingKeywordOrderByIdAsc(keyword, pageRequest);

        for(User keywordUser: keywordUserList) {
            Long checkUserId = keywordUser.getUserId();
            int appear = 1;
            if(loginUser.getUserId() == checkUserId) {
                log.info("[로그인 유저이면 리스트에 추가 X]");
                continue;
            }
            List<Object[]> results = userRepository.findUsersByFamilyIdAndUserId(familyId, checkUserId);
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
     * 초대 리스트 확인 API
     * [GET] /users/invitation
     * @return List<GetInvitationRes>: 회원이 받은 초대 요청 리스트
     */
    @Transactional
    public List<GetInvitationRes> getInvitationList(User loginUser){
        List<GetInvitationRes> getInvitationResList = new ArrayList<>();
        List<UserFamily> userFamilyList = userFamilyRepository.findAllByUserIdOrderByCreatedAtDesc(loginUser);

//        // TODO: 받은 초대가 없을 경우 예외처리
//        if (userFamilyList.isEmpty()) {
//            throw new InternalServerErrorException("초대 요청이 존재하지 않습니다.");
//        } else {
            for (UserFamily invitation : userFamilyList) {
                GetInvitationRes getInvitationRes = new GetInvitationRes(invitation.getFamilyId().getFamilyName(),
                        // invitation.getFamilyId().getOwner().getNickname(),
                        // invitation.getFamilyId().getOwner().getProfileImg());
                        invitation.getInviteUserId().getNickname(),
                        invitation.getInviteUserId().getProfileImg());

                getInvitationResList.add(getInvitationRes);
            }
//        }

        return getInvitationResList;
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
     * 비밀번호 재설정 API
     * [PATCH]
     * @return
     */
    public void updatePasswordWithoutLogin(PatchPwdWithoutLoginReq patchPwdWithoutLoginReq, String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new InternalServerErrorException("아이디가 일치하지 않습니다."));

        user.updatePassword(passwordEncoder.encode(patchPwdWithoutLoginReq.getPasswordA()));
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
    public void deleteUser(User user) throws IllegalAccessException {
        Long userId = user.getUserId();
        //1) 가족 생성자면 예외처리
        List<Family> ownerFamilies = familyRepository.findByOwner(user);
        if(ownerFamilies != null) {
            for(Family f : ownerFamilies) {
                throw new IllegalAccessException("["+f.getFamilyName()+"] 속 생성자 권한을 다른 사람에게 넘기고 탈퇴해야 합니다.");
            }
        }
        //2) 로그인 유저의 댓글 좋아요 일괄 삭제
        List<CommentLove> commentLoves = commentLoveWithUserRepository.findCommentLovesByUserId(userId);
        if(commentLoves != null) {
            commentLoveWithUserRepository.deleteAll(commentLoves);
        }
        //3) 로그인 유저의 게시글 좋아요 일괄 삭제
        List<PostLove> postLoves = postLoveRepository.findPostLovesByUserId(userId);
        if(postLoves != null) {
            postLoveRepository.deleteAll(postLoves);
        }

        //1. 로그인 유저의 댓글 일괄 삭제
        //1-1. 그 전에 로그인 유저가 작성한 댓글 속 좋아요들 일괄 삭제
        List<Comment> comments = commentWithUserRepository.findCommentsByUserId(userId);
        if(comments != null) {
            commentWithUserRepository.deleteAll(comments);
        }
        //2. 로그인 유저의 게시글 일괄 삭제
        //2-1. 그 전에 로그인 유저가 작성한 게시글 속 댓글들 일괄 삭제
        List<Comment> commentsInPosts = commentWithUserRepository.findByPostUserID(userId);
        //2-1-1. 그 전에 로그인 유저가 작성한 게시글 속 댓글들 속 좋아요들 일괄 삭제
        List<CommentLove> commentLovesInComments = commentLoveWithUserRepository.findCommentLovesByCommentUserId(userId);
        if(commentLovesInComments != null) {
            commentLoveWithUserRepository.deleteAll(commentLovesInComments);
        }
        if(commentsInPosts != null) {
            commentWithUserRepository.deleteAll(commentsInPosts);
        }
        //2-2. 그 전에 로그인 유저가 작성한 게시글 속 좋아요를 일괄 삭제
        List<PostLove> postLovesInPosts = postLoveRepository.findPostLovesByPostUserId(userId);
        if(postLovesInPosts != null) {
            postLoveRepository.deleteAll(postLovesInPosts);
        }
        List<Post> posts = postWithUserRepository.findPostByUserId(userId);
        if(posts != null) {
            postWithUserRepository.deleteAll(posts);
        }
        //4) 로그인 유저의 참여한 유저가족매핑 삭제
        List<UserFamily> userFamilyList = userFamilyRepository.findUserFamilyByUserId(userId);
        if(userFamilyList != null) {
            userFamilyRepository.deleteAll(userFamilyList);
        }
        //5) 로그인 유저 삭제
        userRepository.deleteById(userId);
    }
}