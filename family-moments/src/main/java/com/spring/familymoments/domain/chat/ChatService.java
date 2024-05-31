package com.spring.familymoments.domain.chat;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.chat.document.ChatDocument;
import com.spring.familymoments.domain.chat.model.ChatMemberInfo;
import com.spring.familymoments.domain.chat.model.ChatRoomInfo;
import com.spring.familymoments.domain.chat.model.MessageReq;
import com.spring.familymoments.domain.chat.model.MessageRes;
import com.spring.familymoments.domain.common.UserFamilyRepository;
import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.family.FamilyRepository;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.ChatProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.spring.familymoments.config.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private static final int MAXIMUM_MESSAGE = 300;
    private static final int MESSAGE_PAGE = 30;

    private final ChatDocumentRepository chatDocumentRepository;
    private final SessionService sessionService;
    private final FamilyRepository familyRepository;
    private final UserFamilyRepository userFamilyRepository;

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

    // 메세지 목록 조회 - 읽지 않은 메세지(마지막 접속 기록 기준)
    public List<MessageRes> getUnreadMessages(User user, Long familyId) {
        // TODO: family가 아닌 userFamily 받아오기
        Family family = familyRepository.findById(familyId).orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));
        // TODO: family 유효성 검증

        LocalDateTime lastAccessedTime = sessionService.getLastAccessedTime(user, family);
        List<ChatDocument> chatDocuments = chatDocumentRepository
                .findByFamilyIdAndSendedTimeAfterOrderBySendedTimeDesc(familyId, lastAccessedTime, PageRequest.of(0, MAXIMUM_MESSAGE));

        List<MessageRes> messages = chatDocuments.stream()
                .map(message -> MessageRes.builder()
                        .messageId(message.getId().toString())
                        .sender(message.getSender())
                        .message(message.getMessage())
                        .sendedTime(message.getSendedTime())
                        .build())
                .collect(Collectors.toList());

        return messages;
    }

    // 메세지 목록 조회 - messageId 이전 메세지
    public List<MessageRes> getPreviousMessages(User user, Long familyId, String messageId) {
        // TODO: family 유효성 검증

        List<ChatDocument> chatDocuments = chatDocumentRepository.findByFamilyIdAndIdBeforeOrderByIdDesc(
                familyId, new ObjectId(messageId), PageRequest.of(0, MESSAGE_PAGE)
        );

        List<MessageRes> messages = chatDocuments.stream()
                .map(message -> MessageRes.builder()
                        .messageId(message.getId().toString())
                        .sender(message.getSender())
                        .message(message.getMessage())
                        .sendedTime(message.getSendedTime())
                        .build())
                .collect(Collectors.toList());

        return messages;
    }

    // 채팅방 목록 조회
    public List<ChatRoomInfo> getMyChatRooms(User user) {
        List<UserFamily> userFamilyList = userFamilyRepository.findUserFamilyByUserId(Optional.of(user));

        List<ChatRoomInfo> chatRoomInfos = userFamilyList.stream()
                .map(userFamily -> {
                    Family family = userFamily.getFamilyId();

                    long cntMsg = chatDocumentRepository.countByFamilyIdAndSendedTimeAfter(family.getFamilyId(), userFamily.getLastAccessedTime());

                    return ChatRoomInfo.builder()
                            .familyId(family.getFamilyId())
                            .familyName(family.getFamilyName())
                            .familyProfile(family.getRepresentImg())
                            .unreadMessages((cntMsg > 300L) ? 300 : Long.valueOf(cntMsg).intValue())
                            .lastMessage(chatDocumentRepository.findFirstByFamilyIdOrderByIdDesc(family.getFamilyId()).getMessage())
                            .build();
                })
                .collect(Collectors.toList());

        return chatRoomInfos;
    }

    // 현재 채팅방 정보 조회 - 유저 정보, 채팅방 정보
    public ChatMemberInfo getChatRoomInfo(User user, Long familyId) {
        // TODO: family 유효성 검사
        Family family = familyRepository.findById(familyId).orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        List<User> members = userFamilyRepository.findActiveUsersByFamilyId(familyId);
        List<ChatProfile> chatProfiles = members.stream()
                .filter(member -> !member.equals(user))
                .map(member -> ChatProfile.builder()
                        .id(member.getId())
                        .nickname(member.getNickname())
                        .profileImg(member.getProfileImg())
                        .build())
                .collect(Collectors.toList());

        return ChatMemberInfo.builder()
                .familyId(familyId)
                .familyName(family.getFamilyName())
                .members(chatProfiles)
                .build();
    }
}
