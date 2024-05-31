package com.spring.familymoments.domain.chat;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.common.UserFamilyRepository;
import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.redis.RedisService;
import com.spring.familymoments.domain.user.UserRepository;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
    private static final String PREFIX_SESSION_ID = "SI)";
    private static final String PREFIX_FAMILY_ID = "FM)";
    private static final String SUBPREFIX_FAMILY_SUB = "SUB-";
    private static final String SUBPREFIX_FAMILY_UNSUB = "UNSUB-";
    private static final String SUBPREFIX_FAMILY_OFF = "OFF-";

    private final RedisService redisService;
    private final UserFamilyRepository userFamilyRepository;

    // 연결
    public void connect(String sessionId, User user) {
        saveSessionInfo(sessionId, user.getUuid());

        List<UserFamily> userFamilyList = userFamilyRepository.findUserFamilyByUserId(user.getUserId());

        for(UserFamily userFamily : userFamilyList) {
            Family family = userFamily.getFamilyId();

            // offline에서 제거
            redisService.removeMember(PREFIX_FAMILY_ID + SUBPREFIX_FAMILY_OFF + family.getFamilyId(), user.getUuid());

            // unsub 상태로 변경
            redisService.addValues(PREFIX_FAMILY_ID + SUBPREFIX_FAMILY_UNSUB + family.getFamilyId(), user.getUuid());
        }
    }

    // 접속한 유저의 세션 정보 저장
    public void saveSessionInfo(String sessionId, String userId) {
        redisService.setValues(PREFIX_SESSION_ID + sessionId, userId);
    }
}
