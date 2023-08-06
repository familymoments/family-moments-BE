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
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.spring.familymoments.config.BaseResponseStatus.*;
import static com.spring.familymoments.domain.common.entity.UserFamily.Status.*;

@Service
@RequiredArgsConstructor
@Transactional
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final UserFamilyRepository userFamilyRepository;
    private final UserRepository userRepository;
    private final PostWithUserRepository postWithUserRepository;
    private final CommentWithUserRepository commentWithUserRepository;

    // 가족 생성하기
    public PostFamilyRes createFamily(User owner, PostFamilyReq postFamilyReq, String fileUrl) throws BaseException{

//        // 1. 가족 튜플 생성
//        // 유저 외래키 생성
//        User owner = userRepository.findById(userId)
//                .orElseThrow(() -> new BaseException(FIND_FAIL_USERNAME));

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
                owner.getNickname()
        );

    }


    //특정 가족 정보 조회
    public FamilyDto getFamily(Long id){
        Optional<Family> family = familyRepository.findById(id);

        if (family.isEmpty()) {
            throw new NoSuchElementException("존재하지 않습니다");
        }

        return FamilyDto.builder()
                .owner(family.get().getOwner().getNickname())
                .familyName(family.get().getFamilyName())
                .uploadCycle(family.get().getUploadCycle())
                .inviteCode(family.get().getInviteCode())
                .representImg(family.get().getRepresentImg())
                .build();

    }

    // 닉네임 및 가족 생성일 조회
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
    public FamilyDto getFamilyByInviteCode(String inviteCode){
        Optional<Family> family = familyRepository.findByInviteCode(inviteCode);

        return family.map(value -> FamilyDto.builder()
                .owner(value.getOwner().getNickname())
                .familyName(value.getFamilyName())
                .uploadCycle(value.getUploadCycle())
                .inviteCode(value.getInviteCode())
                .representImg(value.getRepresentImg())
                .build()).orElse(null);

    }


    // 가족 초대
    public void inviteUser(User user, List<String> userIdList, Long familyId) throws IllegalAccessException {
        Optional<Family> familyOptional = familyRepository.findById(familyId);
        // 1. 리스트의 유저들이 family에 가입했는지 확인
        // 가입 -> 테이블에 존재&&상태 ACTIVE
        if (familyOptional.isPresent()) {
            Family family = familyOptional.get();

            for (String ids : userIdList) {

                // 매핑 테이블에 존재하는지 확인
                List<UserFamily> byUserIdList = userFamilyRepository.findUserFamilyByUserId(Optional.ofNullable(userRepository.findByNickname(ids)));
                if(byUserIdList.size() != 0){
                    for (UserFamily userFamily : byUserIdList) {
                        // 이미 다른 가족에 초대 대기 중이거나 초대 당한 사람
                        if(userFamily.getStatus() == ACTIVE || userFamily.getStatus() == DEACCEPT){
                            throw new IllegalAccessException("이미 초대 요청을 받은 회원입니다.");
                        }
                    }
                }

                User invitedUser = userRepository.findByNickname(ids);

                if(invitedUser == null){
                    throw new NoSuchElementException("사용자를 찾을 수 없습니다.");
                }

                UserFamily userFamily = UserFamily.builder()
                        .familyId(family)
                        .userId(invitedUser)
                        .inviteUserId(user)
                        .status(DEACCEPT).build();

                userFamilyRepository.save(userFamily);

//                if (byUserId.isPresent() && byUserId.get().getStatus() == ACTIVE) {
//                    throw new IllegalAccessException("이미 가족에 가입된 회원입니다.");
//                }if(byUserId.isPresent() && byUserId.get().getStatus() == DEACCEPT && byUserId.get().getFamilyId() == family){
//                    throw new IllegalAccessException("이미 초대 요청을 보낸 회원입니다.");
//                } else {
//                    User user = userRepository.findById(ids)
//                            .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));
//
//                    UserFamily userFamily = UserFamily.builder()
//                            .familyId(family)
//                            .userId(user)
//                            .status(DEACCEPT).build();
//
//                    userFamilyRepository.save(userFamily);
//                }
            }
        } else {
            throw new NoSuchElementException("가족을 찾을 수 없습니다.");
        }
    }

    // 가족 초대 수락
    public void acceptFamily(User user, Long familyId){
        Optional<Family> familyOptional = familyRepository.findById(familyId);

        if (familyOptional.isPresent()) {
            Family family = familyOptional.get();

            // 1. 매핑 테이블에서 userId와 familyId로 검색
            Optional<UserFamily> userFamily = userFamilyRepository.findByUserIdAndFamilyId(user, family);

            if (userFamily.isPresent()) {
                // 2. 상태 바꿔줌
                UserFamily updatedUserFamily = userFamily.get().toBuilder()
                        .status(ACTIVE)
                        .build();

                userFamilyRepository.save(updatedUserFamily);
            } else {
                throw new NoSuchElementException("존재하지 않는 초대 내역입니다.");
            }
        } else {
            throw new NoSuchElementException("존재하지 않는 사용자 또는 가족입니다.");
        }
    }

    // 업로드 주기 수정
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
    public void deleteFamily(User user, Long familyId) throws BaseException{
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new BaseException(FIND_FAIL_USERNAME));

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
    public FamilyDto updateFamily(User user, Long familyId, FamilyUpdateDto familyUpdateDto) throws IllegalAccessException {
        //Optional<User> userOptional = userRepository.findById(userId);
        Optional<Family> familyOptional = familyRepository.findById(familyId);
        //1. 매핑 테이블에서 userId와 familyId로 검색
        //2. userId 변경
        if (familyOptional.isPresent()) {
            Family family = familyOptional.get();
            // 유저 권환 확인
            if(!user.equals(family.getOwner())){
                throw new IllegalAccessException("권한이 없습니다.");
            }

            family.updateFamily(userRepository.findByNickname(familyUpdateDto.getOwner()), familyUpdateDto.getFamilyName());
            if(family.getOwner() == null){
                throw new NoSuchElementException("존재하지 않는 사용자 입니다.");
            }
            familyRepository.save(family);

            return FamilyDto.builder()
                    .owner(family.getOwner().getNickname())
                    .familyName(family.getFamilyName())
                    .uploadCycle(family.getUploadCycle())
                    .inviteCode(family.getInviteCode())
                    .representImg(family.getRepresentImg())
                    .build();

        } else {
            throw new NoSuchElementException("존재하지 않는 사용자 또는 가족입니다.");
        }
    }

    // 가족 탈퇴
    public void withdrawFamily(User user, Long familyId) throws BaseException{
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        // 1. 게시글 삭제
        List<Post> posts = postWithUserRepository.findPostByUserId(user.getUserId());
        for (Post post : posts) {
            post.updateStatus(BaseEntity.Status.INACTIVE);
        }
        // 2. 댓글 삭제
        List<Comment> comments = commentWithUserRepository.findCommentsByUserId(user.getUserId());
        for (Comment comment : comments) {
            comment.updateStatus(Comment.Status.INACTIVE);
        }
        // 3. 매핑 테이블에서 유저-가족 정보 삭제
        Optional<UserFamily> byUserIdAndFamilyId = userFamilyRepository.findByUserIdAndFamilyId(user, family);
        if(byUserIdAndFamilyId.isEmpty()){
            throw new NoSuchElementException("가족에 가입되어 있지 않은 유저입니다.");
        }

        // 매핑테이블에서 삭제
        userFamilyRepository.delete(byUserIdAndFamilyId.get());
    }

    // 가족 강제 탈퇴
    public void emissionFamily(User user, Long familyId, List<String> userIdList) throws BaseException{

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        // 생성자 권한 확인
        if (!family.getOwner().equals(user)) {
            throw new BaseException(FAILED_USERSS_UNATHORIZED);
        }

        for (String ids : userIdList) {
            User emissionUser = userRepository.findByNickname(ids);
            // 유저 탈퇴
            withdrawFamily(emissionUser, familyId);
        }

    }


}
