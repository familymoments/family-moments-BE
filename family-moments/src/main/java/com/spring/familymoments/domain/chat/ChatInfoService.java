package com.spring.familymoments.domain.chat;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponseStatus;
import com.spring.familymoments.domain.common.UserFamilyRepository;
import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.family.FamilyRepository;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.user.UserRepository;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.spring.familymoments.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class ChatInfoService {
    private final UserRepository userRepository;
    private final UserFamilyRepository userFamilyRepository;
    private final FamilyRepository familyRepository;

    @Transactional
    public void renewLastAccessedTime(String userId, Long familyId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(FIND_FAIL_USER));
        Family family = familyRepository.findById(familyId).orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        renewLastAccessedTime(user, family);
    }

    @Transactional
    public void renewLastAccessedTime(User user, Family family) {
        UserFamily userFamily = userFamilyRepository.findByUserIdAndFamilyId(user, family)
                .orElseThrow(()-> new BaseException(minnie_FAMILY_INVALID_USER));

        userFamily.updateLastAccessedTime(LocalDateTime.now());

        userFamilyRepository.save(userFamily);
    }

    @Transactional
    public LocalDateTime getLastAccessedTime(User user, Family family) {
        UserFamily userFamily = userFamilyRepository.findByUserIdAndFamilyId(user, family)
                .orElseThrow(()-> new BaseException(minnie_FAMILY_INVALID_USER));

        return userFamily.getLastAccessedTime();
    }
}
