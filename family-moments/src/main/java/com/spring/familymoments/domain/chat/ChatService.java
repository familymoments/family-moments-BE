package com.spring.familymoments.domain.chat;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponseStatus;
import com.spring.familymoments.domain.chat.document.ChatDocument;
import com.spring.familymoments.domain.chat.model.ChatRoomInfo;
import com.spring.familymoments.domain.chat.model.MessageReq;
import com.spring.familymoments.domain.chat.model.MessageRes;
import com.spring.familymoments.domain.family.FamilyRepository;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.redis.RedisService;
import com.spring.familymoments.domain.user.UserRepository;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private static final String PREFIX_USER_ID = "UI)";
    private static final String PREFIX_SESSION_ID = "SI)";
    private static final String PREFIX_FAMILY_ID = "FM";

    private final ChatDocumentRepository chatDocumentRepository;
    private final RedisService redisService;
    private final ChatInfoService chatInfoService;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;

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
                .messageId(chatDocument.getId().toString())
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
        String userId = redisService.getValues(PREFIX_SESSION_ID + sessionId);

        // user의 가족 목록 load, redis에서 connect 여부 확인 -> 연결 종료되지 않은 내역이 있다면 lastAccessedTime 갱신
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new BaseException(BaseResponseStatus.FIND_FAIL_USER);
        });
        List<Family> familyList = familyRepository.findActiveFamilyByUserId(user);

        for(Family family : familyList) {
            String key = PREFIX_FAMILY_ID + family.getFamilyId() + ":";
            Set<String> online_members = redisService.getMembers(key);

            if(online_members.contains(userId)) {
                redisService.removeMember(key, userId);
                chatInfoService.renewLastAccessedTime(user, family);
            }
        }

        // disconnect 시 session 정보 삭제
        redisService.deleteValues(PREFIX_SESSION_ID + sessionId);
        redisService.deleteValues(PREFIX_USER_ID + userId);
    }


    // 현재 접속 중 멤버 리스트에 user를 추가
    public void enterChatRoom(String userId, String familyId) {
        String key = PREFIX_FAMILY_ID + familyId + ")";
        redisService.addValues(key, userId);
    }

    // 현재 접속 중 멤버 리스트에서 user를 제외
    public void leaveChatRoom(String userId, String familyId) {
        String key = PREFIX_FAMILY_ID + familyId + ")";
        redisService.removeMember(key, userId);
        chatInfoService.renewLastAccessedTime(userId, Long.valueOf(familyId));
    }

    // 메세지 목록 조회 - 읽지 않은 메세지(마지막 접속 기록 기준)
    public List<MessageRes> getUnreadMessages(User user, Long familyId) {
        return null;
    }

    // 메세지 목록 조회 - messageId 이전 메세지
    public List<MessageRes> getPreviousMessages(User user, Long familyId, String messageId) {
        return null;
    }

    // 채팅방 목록 조회
    public List<ChatRoomInfo> getMyChatRooms(User user) {
        return null;
    }

    // 현재 채팅방 정보 조회 - 유저 정보, 채팅방 정보
    public ChatRoomInfo getChatRoomInfo(User user, Long familyId) {
        return null;
    }
}
