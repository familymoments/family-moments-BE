package com.spring.familymoments.domain.chat;

import com.spring.familymoments.domain.chat.document.ChatDocument;
import com.spring.familymoments.domain.chat.model.MessageReq;
import com.spring.familymoments.domain.chat.model.MessageRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatDocumentRepository chatDocumentRepository;

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

    public void sendAlarm(long familyId, MessageRes messageRes) {
        // TODO: Online-unsub 유저에게 알림 발송
        // TODO: offline 유저에게 알림 발송
    }
}
