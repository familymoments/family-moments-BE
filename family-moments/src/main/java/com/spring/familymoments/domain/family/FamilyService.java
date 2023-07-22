package com.spring.familymoments.domain.family;

import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.family.model.FamilyDto;
import com.spring.familymoments.domain.family.model.PostFamilyReq;
import com.spring.familymoments.domain.family.model.PostFamilyRes;
import com.spring.familymoments.domain.user.UserService;
import com.spring.familymoments.domain.user.entity.User;
import com.sun.xml.bind.v2.TODO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final UserService userService;

    public PostFamilyRes createFamily(User owner, PostFamilyReq postFamilyReq) {
        Family family = Family.builder()
                .owner(owner)
                .familyName(postFamilyReq.getFamilyName())
                .uploadCycle(postFamilyReq.getUploadCycle())
                .inviteCode("1111111")
                .representImg(postFamilyReq.getRepresentImg())
                .build();
        Family saveFamily = familyRepository.save(family);

        return new PostFamilyRes(saveFamily.getFamilyId(), owner.getNickname(), saveFamily.getInviteCode(), owner.getProfileImg(), saveFamily.getRepresentImg(), saveFamily.getCreatedAt());
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

}
