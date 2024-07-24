package com.spring.familymoments.domain.chat;

import com.spring.familymoments.domain.chat.model.MessageReq;
import com.spring.familymoments.domain.chat.model.MessageRes;
import com.spring.familymoments.domain.chat.model.MessageTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

@Controller
@MessageMapping("")
@RequiredArgsConstructor
public class StompController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;

    @MessageMapping("{familyId}.send")
    public void handleSend(@DestinationVariable("familyId") long familyId, MessageReq messageReq) {
        MessageRes messageRes = chatService.createChat(familyId, messageReq);
        MessageTemplate response = new MessageTemplate(MessageTemplate.MessageType.MESSAGE, messageRes);

        simpMessagingTemplate.convertAndSend("/sub/" + familyId, response);

        // TODO: online-unsub & offline 유저에게 알림 발송
        chatService.sendAlarm(familyId, messageRes);
    }
}
