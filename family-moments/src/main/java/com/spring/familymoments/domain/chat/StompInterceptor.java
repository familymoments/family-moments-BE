package com.spring.familymoments.domain.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {
    private static final String NOTIFICATION = "notification";

    private final ChatService chatService;

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        StompCommand command = headerAccessor.getCommand();
        String destination = headerAccessor.getDestination();

        if(command.equals(StompCommand.SUBSCRIBE)) {
            if(destination.contains(NOTIFICATION)) { // 알림 채널 구독
                String userId = destination.replace("/sub/", "").replace("/notification", "");
                String sessionId = headerAccessor.getSessionId();

                chatService.saveSessionInfo(userId, sessionId);
            } else { // 가족 채팅방 구독
                String subscriptionId = headerAccessor.getSubscriptionId();
                String userId = subscriptionId.substring(0, subscriptionId.lastIndexOf("-"));
                String familyId = subscriptionId.substring(subscriptionId.lastIndexOf("-") + 1);

                chatService.enterChatRoom(userId, familyId);
            }
        } else if(command.equals(StompCommand.UNSUBSCRIBE) && !destination.contains(NOTIFICATION)) { // 가족 채팅 구독 해제
            String subscriptionId = headerAccessor.getSubscriptionId();
            String userId = subscriptionId.substring(0, subscriptionId.lastIndexOf("-"));

            chatService.leaveChatRoom(userId, subscriptionId.substring(subscriptionId.lastIndexOf("-") + 1));
        } else if(command.equals(StompCommand.DISCONNECT)) { // 연결 해제
            String sessionId = headerAccessor.getSessionId();
            chatService.deleteSessionInfo(sessionId);

            return null;
        }

        return message;
    }
}
