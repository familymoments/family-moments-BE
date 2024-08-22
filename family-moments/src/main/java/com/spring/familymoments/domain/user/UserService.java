package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.secret.jwt.JwtService;
import com.spring.familymoments.domain.alarmSetting.AlarmSettingRepository;
import com.spring.familymoments.domain.alarmSetting.AlarmSettingService;
import com.spring.familymoments.domain.alarmSetting.entity.AlarmSetting;
import com.spring.familymoments.domain.comment.CommentReportRepository;
import com.spring.familymoments.domain.comment.CommentWithUserRepository;
import com.spring.familymoments.domain.comment.entity.Comment;
import com.spring.familymoments.domain.comment.entity.CommentReport;
import com.spring.familymoments.domain.commentLove.CommentLoveWithUserRepository;
import com.spring.familymoments.domain.commentLove.entity.CommentLove;
import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.common.UserFamilyRepository;
import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.family.FamilyRepository;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.fcm.FCMService;
import com.spring.familymoments.domain.post.PostReportRepository;
import com.spring.familymoments.domain.post.PostWithUserRepository;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.post.entity.PostReport;
import com.spring.familymoments.domain.postLove.PostLoveRepository;
import com.spring.familymoments.domain.postLove.entity.PostLove;
import com.spring.familymoments.domain.redis.RedisService;
import com.spring.familymoments.domain.socialInfo.*;
import com.spring.familymoments.domain.socialInfo.entity.SocialInfo;
import com.spring.familymoments.domain.socialInfo.model.GoogleDeleteDto;
import com.spring.familymoments.domain.user.model.*;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.utils.UuidUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.spring.familymoments.config.BaseResponseStatus.*;
import static com.spring.familymoments.domain.common.BaseEntity.Status.INACTIVE;
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
    private final RedisService redisService;
    private final AlarmSettingService alarmSettingService;

    private final PostReportRepository postReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final SocialUserRepository socialUserRepository;
    private final AlarmSettingRepository alarmSettingRepository;

    private final FCMService fcmService;

    /**
     * createUser
     * [POST]
     * @return PostUserRes
     */
    // TODO: [중요] 로그인 API 구현 후 JWT Token 반환하는 부분 제거하기!
    @Transactional
    public PostUserRes createUser(PostUserReq.joinUser postUserReq) throws BaseException {
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

        User user = User.builder()
                .id(postUserReq.getId())
                .uuid(uuid)
                .email(postUserReq.getEmail())
                .password(passwordEncoder.encode(postUserReq.getPassword()))
                .nickname(postUserReq.getNickname())
                .profileImg(postUserReq.getProfileImg())
                .status(User.Status.ACTIVE)
                .build();

        userRepository.save(user);
        alarmSettingService.createAlarmSetting(user);   // 알림 ON으로 설정(채팅알림, 업로드주기알림, 포스팅알림)

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
    /*public boolean checkDuplicateId(String UserId) throws BaseException {
        return userRepository.existsById(UserId);
    }*/

    /**
     * 아이디 중복 확인 ACTIVE 포함 ver.
     * [GET]
     * @return 이미 가입된 아이디면 -> true, 그렇지 않으면 -> false
     */
    public boolean checkDuplicateIdByStatus(String userId) {
        Optional<User> activeUser = userRepository.findById(userId);
        return activeUser.isPresent();
    }

//    /**
//     * INACTIVE 여부 확인
//     * [GET] 가입된 아이디가 존재하나, INACTIVE 상태일 경우 같은 아이디로 가입을 허용
//     * @return INACTIVE 상태면 존재하는 아이디로 가입 가능 -> true
//     */
//    public boolean checkInactiveUserById(String UserId) throws BaseException {
//        User member = userRepository.findById(UserId)
//                .orElseThrow(() -> new BaseException(FIND_FAIL_USER_ID));
//
//        return member.getStatus() == User.Status.INACTIVE;
//    }

    /**
     * 이메일 중복 확인 ACTIVE 포함 ver.
     * [GET]
     * @return 이미 가입된 이메일이면 -> true, 그렇지 않으면 -> false
     */
    /*public boolean checkDuplicateEmail(String email) throws BaseException {
        return userRepository.existsByEmail(email);
    }*/

    /**
     * 이메일 중복 확인 ACTIVE 포함 ver.
     * [GET]
     * 이미 가입된 이메일이면 -> true, 그렇지 않으면 false
     * @param email
     * @return
     */
    public boolean checkDuplicateEmailByStatus(String email) {
        Optional<User> activeUser = userRepository.findByEmail(email);
        return activeUser.isPresent();
    }


    /**
     * 회원정보 조회 API
     * [GET]
     * @return
     */
    public GetProfileRes readProfile(User user, Long familyId) {
        Long totalUpload = 0L;
        if(familyId != null) {
            Family family = familyRepository.findById(familyId).orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));
            totalUpload = postWithUserRepository.countActivePostsByWriterAndFamily(user, family);
        }

        LocalDateTime targetDate = user.getCreatedAt(); //가입한 후 경과 일수
        LocalDateTime currentDate = LocalDateTime.now();
        Long duration = ChronoUnit.DAYS.between(targetDate, currentDate);

        return new GetProfileRes(user.getProfileImg(), user.getNickname(), user.getEmail(), totalUpload, duration);
    }
    /**
     * 유저 5명 검색 API
     * [GET] /users
     * @return
     */
    @Transactional
    public List<GetSearchUserRes> searchUserById(String keyword, Long familyId, User loginUser) {
        List<GetSearchUserRes> getSearchUserResList = new ArrayList<>();

        List<User> keywordUserList = userRepository.searchUserByKeyword(keyword);
        for(User keywordUser: keywordUserList) {
            Long checkUserId = keywordUser.getUserId();
            int appear = 1;

            //로그인한 유저 제외
            if(loginUser.getUserId() == checkUserId) {
                continue;
            }

            //현재 가족과 관련된 유저들
            List<Object[]> results = userRepository.findUsersByFamilyIdAndUserId(familyId, checkUserId);
            for(Object[] result : results) {
                UserFamily userFamily = (UserFamily) result[1];
                if (userFamily == null) {
                    continue;
                }
                //현재 가족에 이미 초대 당하거나 대기 중일 때 비활성화
                if(userFamily.getStatus() == ACTIVE || userFamily.getStatus() == DEACCEPT) {
                    appear = 0;
                    break;
                }
            }

            getSearchUserResList.add(
                    GetSearchUserRes.of(
                            keywordUser.getId(),
                            keywordUser.getProfileImg(),
                            appear
                    )
            );
        }

        return getSearchUserResList;
    }
    /**
     * 초대 리스트 확인 API
     * [GET] /users/invitation
     * @return List<GetInvitationRes>: 회원이 받은 초대 요청 리스트
     */
    @Transactional(readOnly = true)
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

        return new PatchProfileReqRes(updatedUser.getNickname(), updatedUser.getProfileImg());
    }
    /**
     * 비밀번호 인증 API
     * [GET]
     * @return true || false
     */
    public boolean authenticate(GetPwdReq getPwdReq, User user) {
        if(getPwdReq.getPassword().isEmpty()) {
            throw new BaseException(EMPTY_PASSWORD);
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
    public void updatePasswordWithoutLogin(PatchPwdWithoutLoginReq patchPwdWithoutLoginReq, String id) throws BaseException {
        User user = userRepository.findById(id).orElseThrow(() -> new BaseException(FIND_FAIL_USER_ID));
        user.updatePassword(passwordEncoder.encode(patchPwdWithoutLoginReq.getPasswordA()));
        userRepository.save(user);
    }
    /**
     * 전체 회원정보 조회 API / 화면 외 API
     * [GET]
     * @return
     */
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    /**
     * 회원 탈퇴 API
     * [DELETE] /users
     *
     * set null (탈퇴한 유저의 댓글 알수없음 / 탈퇴한 유저의 신고 내역(탈퇴한 유저가 신고한) 남김)
     *
     * @return
     */
    //soft delete
    @Transactional
    public void deleteUser(User user) {
        //1) 가족 생성자면 예외처리
        List<Family> ownerFamilies = familyRepository.findByOwner(user);
        if(!ownerFamilies.isEmpty()) {
            //로그인 유저가 가족 생성자 + 가족 내에 본인 혼자일 때는 탈퇴 처리
            for(Family family : ownerFamilies) {
                //가족 ACTIVE 조건 추가
                if(family.getStatus() == BaseEntity.Status.ACTIVE) {
                    List<UserFamily> uf = userFamilyRepository.findUserFamilyByFamilyId(family.getFamilyId());
                    if(uf.size() == 1) {
                        //가족 삭제 후 탈퇴 진행
                        family.updateStatus(INACTIVE);
                        familyRepository.save(family);
                        continue;
                    }
                    //생성자 권한을 다른 사람에게 넘겨야 탈퇴 가능
                    throw new BaseException(FAILED_TO_LEAVE);
                }
            }
        }
        commonDeleteProcess(user);
    }

    public void commonDeleteProcess(User user) {
        Long userId = user.getUserId();

        //2) 로그인 유저의 댓글 좋아요 일괄 INACTIVE
        List<CommentLove> commentLoves = commentLoveWithUserRepository.findCommentLovesByUserId(userId);
        for(CommentLove commentLove : commentLoves) {
            commentLove.updateStatus(INACTIVE);
        }

        //3) 로그인 유저의 게시글 좋아요 일괄 INACTIVE
        List<PostLove> postLoves = postLoveRepository.findPostLovesByUserId(userId);
        for(PostLove postLove : postLoves) {
            postLove.updateStatus(INACTIVE);
        }

        //4) 로그인 유저의 댓글 '알수없음' 처리 (일괄 INACTIVE)
        List<Comment> comments = commentWithUserRepository.findCommentsByUserId(userId);
        for(Comment comment : comments) {
            comment.updateWriter();
        }

        //5) 로그인 유저의 게시글 일괄 INACTIVE
        List<Post> posts = postWithUserRepository.findPostByUserId(userId);
        for(Post post : posts) {
            post.updateStatus(INACTIVE);
        }

        //6) 로그인 유저의 참여한 유저가족매핑 일괄 INACTIVE
        List<UserFamily> userFamilyList = userFamilyRepository.findUserFamilyByUserId(userId);
        for(UserFamily userFamily : userFamilyList) {
            userFamily.updateStatus(UserFamily.Status.INACTIVE);
        }

        //7) 소셜 정보 INACTIVE
        List<SocialInfo> socialInfos = socialUserRepository.findSocialInfoByUser(user);
        if(!socialInfos.isEmpty()) {
            for (SocialInfo socialInfo : socialInfos) {
                socialInfo.updateStatus(INACTIVE);
            }
        }

        //8) 알람 세팅 INACTIVE
        List<AlarmSetting> alarmSettingList = alarmSettingRepository.findAlarmSettingByUser(user);
        for(AlarmSetting alarmSetting : alarmSettingList) {
            alarmSetting.setStatus(INACTIVE);
        }

        //9) 게시글, 댓글 신고 관련 user null 처리 (탈퇴한 유저가 신고했던 내용만 남기기) -
        List<CommentReport> commentReportList = commentReportRepository.findCommentReportByUser(user);
        for(CommentReport commentReport : commentReportList) {
            commentReport.updateUser();
        }
        List<PostReport> postReportList = postReportRepository.findPostReportByUser(user);
        for(PostReport postReport : postReportList) {
            postReport.updateUser();
        }

        //10) 로그인 유저 INACTIVE
        user.updateStatus(User.Status.INACTIVE);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUserWithRedisProcess(User user, String requestAccessToken) {
        this.deleteUser(user);
        //Redis에 저장되어 있는 RT 삭제
        String refreshTokenInRedis = redisService.getValues("RT(" + "SERVER" + "):" + user);
        if(refreshTokenInRedis != null) {
            redisService.deleteValues("RT(" + "SERVER" + "):" + user);
        }
        //Redis에 탈퇴 처리한 AT 저장
        long expiration = jwtService.getTokenExpirationTime(requestAccessToken) - new Date().getTime();
        redisService.setValuesWithTimeout(requestAccessToken, "delete", expiration);

        fcmService.deleteToken(user.getId());     // FCM Token 삭제
    }

    @Transactional
    public void reportUser(Long toUserId) {
        User toUser = userRepository.findUserByUserId(toUserId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_USERNAME));

        //누적 횟수 3회차, INACTIVE
        if (toUser.getReported() == 2) {
            deleteReportedUser(toUser);
            return;
        }

        //신고 횟수 업데이트
        toUser.updateReported(toUser.getReported() + 1);
        userRepository.save(toUser);
    }

    @Transactional
    public void deleteReportedUser(User user) {
        //1) 가족 일괄 INACTIVE
        List<Family> familyList = familyRepository.findActiveFamilyByUserId(user);
        for(Family family : familyList) {
            family.updateStatus(INACTIVE);

            //1-1) 가족 내 구성원 모두 INACTIVE
            List<UserFamily> uf = userFamilyRepository.findUserFamilyByFamilyId(family.getFamilyId());
            for(UserFamily uf1 : uf) {
                uf1.updateStatus(UserFamily.Status.INACTIVE);
            }
        }
        commonDeleteProcess(user);
    }

}