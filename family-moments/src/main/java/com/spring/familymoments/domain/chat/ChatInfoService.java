package com.spring.familymoments.domain.chat;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponseStatus;
import com.spring.familymoments.domain.chat.entity.ChatInfo;
import com.spring.familymoments.domain.family.FamilyRepository;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.user.UserRepository;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatInfoService {
    private final ChatInfoRepository chatInfoRepository;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;


    void renewLastAccessedTime(String userId, Long familyId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(BaseResponseStatus.FIND_FAIL_USER));
        Family family = familyRepository.findById(familyId).orElseThrow(() -> new BaseException(BaseResponseStatus.FIND_FAIL_FAMILY));

        renewLastAccessedTime(user, family);
    }

    void renewLastAccessedTime(User user, Family family) {
        ChatInfo chatInfo = chatInfoRepository.findChatInfoByUserAndFamily(user, family).orElseGet(() -> {
            return ChatInfo.builder()
                    .user(user)
                    .family(family)
                    .build();
        });

        chatInfo.updateLastAccessedTime(LocalDateTime.now());

        chatInfoRepository.save(chatInfo);
    }
}
