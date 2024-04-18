package com.spring.familymoments.domain.family;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.comment.CommentWithUserRepository;
import com.spring.familymoments.domain.comment.entity.Comment;
import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.common.UserFamilyRepository;
import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.family.model.*;
import com.spring.familymoments.domain.post.PostWithUserRepository;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.user.UserRepository;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spring.familymoments.config.BaseResponseStatus.*;
import static com.spring.familymoments.domain.common.entity.UserFamily.Status.*;

@Service
@RequiredArgsConstructor
// @Transactional
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final UserFamilyRepository userFamilyRepository;
    private final UserRepository userRepository;
    private final PostWithUserRepository postWithUserRepository;
    private final CommentWithUserRepository commentWithUserRepository;

    private final int MAX_FAMILY_COUNT = 5;

    // 가족 생성하기
    @Transactional
    public PostFamilyRes createFamily(User owner, PostFamilyReq postFamilyReq, String fileUrl) throws BaseException{

//        // 1. 가족 튜플 생성
//        // 유저 외래키 생성
//        User owner = userRepository.findById(userId)
//                .orElseThrow(() -> new BaseException(FIND_FAIL_USERNAME));

        checkFamilyLimit(owner);

        // 초대 링크 생성
        String invitationCode = UUID.randomUUID().toString();
        String inviteLink = "https://family-moments.com/invite/"+ invitationCode;

        // 가족 입력 객체 생성
        Family family = Family.builder()
                .owner(owner)
                .familyName(postFamilyReq.getFamilyName())
                .uploadCycle(postFamilyReq.getUploadCycle())
                .inviteCode(inviteLink)
                .representImg(fileUrl)
                .build();

        // 가족 저장
        Family savedFamily = familyRepository.save(family);


        // 2. 유저 가족 매핑 튜플 생성
        // 가족 외래키 생성
        Family preFamily = familyRepository.findById(savedFamily.getFamilyId())
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        // 유저가족 입력 객체 생성
        UserFamily userFamily = UserFamily.builder()
                .userId(owner)
                .familyId(preFamily)
                .inviteUserId(owner)
                .status(ACTIVE)
                .build();

        // 유저 가족 저장
        userFamilyRepository.save(userFamily);

        // 반환
        return new PostFamilyRes(
                savedFamily.getFamilyId(),
                owner.getNickname(),
                savedFamily.getInviteCode()
        );

    }


    //특정 가족 정보 조회
    @Transactional(readOnly = true)
    public FamilyRes getFamily(Long id){
        Family family = familyRepository.findById(id)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        return family.toFamilyRes();
    }

    // 닉네임 및 가족 생성일 조회
    @Transactional
    public GetFamilyCreatedNicknameRes getFamilyCreatedNickname(User user, Long familyId) throws BaseException{

//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new BaseException(FIND_FAIL_USERNAME));

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        LocalDate createdAt = family.getCreatedAt().toLocalDate();
        LocalDate now = LocalDate.now();

        Period period = Period.between(createdAt, now);

        String daysSinceCreation = String.valueOf(period.getDays()+1);

        return new GetFamilyCreatedNicknameRes(user.getNickname(), daysSinceCreation);
    }

    // 가족원 전체 조회
    @Transactional
    public List<GetFamilyAllRes> getFamilyAll(Long familyId) throws BaseException{
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        List<User> activeUsers = userFamilyRepository.findActiveUsersByFamilyId(familyId);

        List<GetFamilyAllRes> getFamilyAllResList = activeUsers.stream()
                .map(user -> new GetFamilyAllRes(user.getUserId(), user.getNickname(), user.getProfileImg()))
                .collect(Collectors.toList());

        return getFamilyAllResList;
    }

    //초대코드로 가족 조회
    @Transactional(readOnly = true)
    public FamilyRes getFamilyByInviteCode(String inviteCode){
        return familyRepository.findByInviteCode(inviteCode)
                .map(Family::toFamilyRes)
                .orElse(null);
    }


    // 가족 초대
    @Transactional
    public void inviteUser(User user, List<String> userIds, Long familyId){
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        for (String userId : userIds) {
            User inviteUser = userRepository.findById(userId)
                    .orElseThrow(() -> new BaseException(FIND_FAIL_USER));

            Optional<UserFamily> userFamily = userFamilyRepository.findByUserIdAndFamilyId(inviteUser, family);

            userFamily.ifPresentOrElse(
                    existingUserFamily -> {
                        if (existingUserFamily.getStatus() == ACTIVE || existingUserFamily.getStatus() == DEACCEPT) {
                            throw new BaseException("이미 초대 요청을 받은 회원이 있습니다.", HttpStatus.CONFLICT.value());
                        }
                        existingUserFamily.updateStatus(DEACCEPT);
                    },
                    () -> {
                        UserFamily newUserFamily = UserFamily.builder()
                                .familyId(family)
                                .userId(inviteUser)
                                .inviteUserId(user)
                                .status(DEACCEPT)
                                .build();

                        userFamilyRepository.save(newUserFamily);
                    }
            );
        }
    }

    // 가족 초대 수락
    @Transactional
    public void acceptFamily(User user, Long familyId){
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        UserFamily userFamily = userFamilyRepository.findByUserIdAndFamilyId(user, family)
                .orElseThrow(() -> new BaseException("존재하지 않는 초대 내역입니다.", HttpStatus.NOT_FOUND.value()));

        checkFamilyLimit(user);

        userFamily.updateStatus(ACTIVE);
        userFamilyRepository.save(userFamily);
    }

    // 업로드 주기 수정
    @Transactional
    public void updateUploadCycle(User user, Long familyId, int uploadCycle) throws BaseException{
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        // 생성자 권한 확인
        if (!family.getOwner().getUserId().equals(user.getUserId())) {
            throw new BaseException(FAILED_USERSS_UNATHORIZED);
        }

        family.updateUploadCycle(uploadCycle);
        familyRepository.save(family);
    }

    // 가족 삭제
    @Transactional
    public void deleteFamily(User user, Long familyId) throws BaseException{
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        // 생성자 권한 확인
        if (!family.getOwner().getUserId().equals(user.getUserId())) {
            throw new BaseException(FAILED_USERSS_UNATHORIZED);
        }

        // 1. 가족 내 게시글의 댓글 일괄 삭제
        List<Post> postsToDelete = postWithUserRepository.findByFamilyId(family);
        for (Post post : postsToDelete) {
            // 해당 게시글의 댓글들의 상태를 일괄적으로 INACTIVE로 변경
            List<Comment> commentsToDelete = commentWithUserRepository.findByPostId(post);
            for (Comment comment : commentsToDelete) {
                comment.updateStatus(Comment.Status.INACTIVE);
            }
        }

        // 2. 가족 내 게시글 일괄 삭제
        for (Post post : postsToDelete) {
            post.updateStatus(BaseEntity.Status.INACTIVE);
        }

        // 3. 가족 삭제
        family.updateStatus(BaseEntity.Status.INACTIVE);
        familyRepository.save(family);
    }

    //가족 정보 수정
    @Transactional
    public FamilyRes updateFamily(User user, Long familyId, FamilyUpdateRes familyUpdateRes){
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));
        User userToOwner = userRepository.findById(familyUpdateRes.getOwner())
                .orElseThrow(() -> new BaseException(FIND_FAIL_USER));

        if(!user.equals(family.getOwner())){
            throw new BaseException("권한이 없습니다.", HttpStatus.UNAUTHORIZED.value());
        }

        family.updateFamily(userToOwner, familyUpdateRes.getFamilyName());
        familyRepository.save(family);

        return family.toFamilyRes();
    }

    // 가족 탈퇴
    @Transactional
    public void withdrawFamily(User user, Long familyId) throws BaseException{
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        List<Post> posts = postWithUserRepository.findPostByUserId(user.getUserId());
        for (Post post : posts) {
            post.updateStatus(BaseEntity.Status.INACTIVE);
        }

        List<Comment> comments = commentWithUserRepository.findCommentsByUserId(user.getUserId());
        for (Comment comment : comments) {
            comment.updateStatus(Comment.Status.INACTIVE);
        }

        UserFamily userFamily = userFamilyRepository.findByUserIdAndFamilyId(user, family)
                .orElseThrow(() -> new BaseException("가족에 가입되어 있지 않은 유저입니다.", HttpStatus.NOT_FOUND.value()));

        userFamilyRepository.delete(userFamily);
    }

    // 가족 강제 탈퇴
    @Transactional
    public void emissionFamily(User user, Long familyId, List<String> userIds){
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        if (!family.isOwner(user)) {
            throw new BaseException(NOT_FAMILY_OWNER);
        }

        for (String userId : userIds) {
            User emissionUser = userRepository.findById(userId)
                    .orElseThrow(() -> new BaseException(FIND_FAIL_USER));

            withdrawFamily(emissionUser, familyId);
        }
    }

    // 가족 권한 수정
    @Transactional
    public void changeFamilyAuthority(User user, Long familyId, String newOwner){
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        if(!family.isOwner(user)){
            throw new BaseException(NOT_FAMILY_OWNER);
        }

        User userToOwner = userRepository.findById(newOwner)
                .orElseThrow(() -> new BaseException(FIND_FAIL_USER));

        family.updateFamilyOwner(userToOwner);
    }

    @Transactional
    public void joinFamily(User user, Long familyId) {
        checkFamilyLimit(user);

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        Optional<UserFamily> userFamily = userFamilyRepository.findByUserIdAndFamilyId(user, family);

        userFamily.ifPresentOrElse(
                existingUserFamily -> {
                    if (existingUserFamily.getStatus() == ACTIVE) {
                        throw new BaseException("이미 가입된 가족입니다.", HttpStatus.CONFLICT.value());
                    }
                    existingUserFamily.updateStatus(ACTIVE);
                },
                () -> {
                    UserFamily newUserFamily = UserFamily.builder()
                            .familyId(family)
                            .userId(user)
                            .inviteUserId(user)
                            .status(ACTIVE).build();

                    userFamilyRepository.save(newUserFamily);
                }
        );
    }

    // 내 가족 리스트 조회
    @Transactional(readOnly = true)
    public List<MyFamilyRes> getMyFamilies(User user){
        List<Family> activeFamilies = familyRepository.findActiveFamilyByUserId(user);
        List<MyFamilyRes> MyFamilies = new ArrayList<>();

        for (Family myFamily : activeFamilies) {
            MyFamilies.add(myFamily.toMyFamilyRes());
        }

        return MyFamilies;
    }

    private void checkFamilyLimit(User user){
        List<Family> activeFamilies = familyRepository.findActiveFamilyByUserId(user);

        if (activeFamilies.size() >= MAX_FAMILY_COUNT){
            throw new BaseException(FAMILY_LIMIT_EXCEEDED);
        }
    }

    // 가족 이름 조회
    @Transactional(readOnly = true)
    public String getFamilyName(User user, Long familyId) {

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        if(!userFamilyRepository.existsByUserIdAndFamilyId(user, family)) {
            throw new BaseException(FIND_FAIL_USER_IN_FAMILY);
        }

        if (family.getStatus() == BaseEntity.Status.INACTIVE) {
            throw new BaseException(FIND_FAIL_FAMILY);
        }

        return family.getFamilyName();
    }

}
