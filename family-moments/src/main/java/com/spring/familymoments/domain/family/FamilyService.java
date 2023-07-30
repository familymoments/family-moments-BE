package com.spring.familymoments.domain.family;

import com.spring.familymoments.domain.common.UserFamilyRepository;
import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.family.model.FamilyDto;
import com.spring.familymoments.domain.family.model.PostFamilyReq;
import com.spring.familymoments.domain.family.model.PostFamilyRes;
import com.spring.familymoments.domain.user.UserRepository;
import com.spring.familymoments.domain.user.UserService;
import com.spring.familymoments.domain.user.entity.User;
import com.sun.xml.bind.v2.TODO;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static com.spring.familymoments.domain.common.entity.UserFamily.Status.ACTIVE;
import static com.spring.familymoments.domain.common.entity.UserFamily.Status.DEACCEPT;

@Service
@RequiredArgsConstructor
@Transactional
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final UserFamilyRepository userFamilyRepository;
    private final UserRepository userRepository;

    public PostFamilyRes createFamily(Long userId, PostFamilyReq postFamilyReq) {

        // 1. 가족 튜플 생성
        // 유저 외래키 생성
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

        // 초대 링크 생성
        String invitationCode = UUID.randomUUID().toString();
        String inviteLink = "https://family-moments.com/invite/"+ invitationCode;

        // 가족 입력 객체 생성
        Family family = Family.builder()
                .owner(owner)
                .familyName(postFamilyReq.getFamilyName())
                .uploadCycle(postFamilyReq.getUploadCycle())
                .inviteCode(inviteLink)
                .representImg(postFamilyReq.getRepresentImg())
                .build();

        // 가족 저장
        Family savedFamily = familyRepository.save(family);


        // 2. 유저 가족 매핑 튜플 생성
        // 가족 외래키 생성
        Family preFamily = familyRepository.findById(savedFamily.getFamilyId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

        // 유저가족 입력 객체 생성
        UserFamily userFamily = UserFamily.builder()
                .userId(owner)
                .familyId(preFamily)
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

    public FamilyDto getFamilyByInviteCode(String inviteCode){
        Optional<Family> family = familyRepository.findByInviteCode(inviteCode);

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

    // 가족 초대
    public void inviteUser(List<Long> userIdList, Long familyId) throws IllegalAccessException {
        Optional<Family> familyOptional = familyRepository.findById(familyId);
        // 1. 리스트의 유저들이 family에 가입했는지 확인
        // 가입 -> 테이블에 존재&&상태 ACTIVE
        if (familyOptional.isPresent()) {
            Family family = familyOptional.get();

            for (Long ids : userIdList) {
                Optional<UserFamily> byUserId = userFamilyRepository.findByUserId(userRepository.findById(ids));

                if (byUserId.isPresent() && byUserId.get().getStatus() == ACTIVE) {
                    throw new IllegalAccessException("이미 가족에 가입된 회원입니다.");
                }if(byUserId.isPresent() && byUserId.get().getStatus() == DEACCEPT && byUserId.get().getFamilyId() == family){
                    throw new IllegalAccessException("이미 초대 요청을 보낸 회원입니다.");
                } else {
                    User user = userRepository.findById(ids)
                            .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));

                    UserFamily userFamily = UserFamily.builder()
                            .familyId(family)
                            .userId(user)
                            .status(DEACCEPT).build();

                    userFamilyRepository.save(userFamily);
                }
            }
        } else {
            throw new NoSuchElementException("가족을 찾을 수 없습니다.");
        }
    }

    // 가족 초대 수락
    public void acceptFamily(Long userId, Long familyId){
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Family> familyOptional = familyRepository.findById(familyId);

        if (userOptional.isPresent() && familyOptional.isPresent()) {
            User user = userOptional.get();
            Family family = familyOptional.get();

            Optional<UserFamily> userFamily = userFamilyRepository.findByUserIdAndFamilyId(user, family);

            // 1. 매핑 테이블에서 userId와 familyId로 검색
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

}
