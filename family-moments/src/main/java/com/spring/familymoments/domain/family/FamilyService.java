package com.spring.familymoments.domain.family;

import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.family.model.PostFamilyReq;
import com.spring.familymoments.domain.family.model.PostFamilyRes;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FamilyService {

    private final FamilyRepository familyRepository;

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
}
