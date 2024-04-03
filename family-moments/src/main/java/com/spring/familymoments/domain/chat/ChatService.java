package com.spring.familymoments.domain.chat;

import com.spring.familymoments.domain.chat.document.ChatDocument;
import com.spring.familymoments.domain.chat.model.MessageReq;
import com.spring.familymoments.domain.chat.model.MessageRes;
import com.spring.familymoments.domain.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private static final String PREFIX_USER_ID = "UI:";
    private static final String PREFIX_SESSION_ID = "SI:";
    private static final String PREFIX_FAMILY_ID = "FM";

    private final ChatDocumentRepository chatDocumentRepository;
    private final RedisService redisService;

    // chat Document에 저장
    public MessageRes createChat(Long familyId, MessageReq messageReq) {
        ChatDocument chatDocument = ChatDocument.builder()
                .familyId(familyId)
                .sender(messageReq.getSender())
                .message(messageReq.getMessage())
                .sendedTime(LocalDateTime.now())
                .build();

        chatDocument = chatDocumentRepository.save(chatDocument);

        MessageRes messageRes = MessageRes.builder()
                .type(MessageRes.MessageType.MESSAGE)
                .sender(chatDocument.getSender())
                .message(chatDocument.getMessage())
                .sendedTime(chatDocument.getSendedTime())
                .build();

        return messageRes;
    }

    // 접속한 유저의 세션 정보 저장
    public void saveSessionInfo(String userId, String sessionId) {
        redisService.setValues(PREFIX_USER_ID + userId, sessionId);
        redisService.setValues(PREFIX_SESSION_ID + sessionId, userId);
    }

    // 유저의 세션 정보 삭제
    public void deleteSessionInfo(String sessionId) {
        String userId = String.valueOf(redisService.getValues(PREFIX_SESSION_ID + sessionId));

        // user의 가족 목록 load, redis에서 connect 여부 확인

        // disconnect 시 session 정보 삭제
        redisService.deleteValues(PREFIX_SESSION_ID + sessionId);
        redisService.deleteValues(PREFIX_USER_ID + userId);
    }


    // 현재 접속 중 멤버 리스트에 user를 추가
    public void enterChatRoom(String userId, String familyId) {
        String key = PREFIX_FAMILY_ID + familyId + ":";
        redisService.addValues(key, userId);
    }

    // 현재 접속 중 멤버 리스트에서 user를 제외
    public void leaveChatRoom(String userId, String familyId) {
        String key = PREFIX_FAMILY_ID + familyId + ":";
        redisService.removeMember(key, userId);
    }
}
