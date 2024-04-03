package com.spring.familymoments.domain.chat;

import com.spring.familymoments.domain.chat.entity.ChatInfo;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatInfoRepository extends JpaRepository<ChatInfo, Long> {
    Optional<ChatInfo> findChatInfoByUserAndFamily(User user, Family family);
}
