package com.spring.familymoments.domain.chat;

import com.spring.familymoments.domain.user.UserService;
import com.spring.familymoments.domain.user.entity.User;
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

    private final SessionService sessionService;
    private final UserService userService; // TODO: 임시, 차후 삭제

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        StompCommand command = headerAccessor.getCommand();
        String destination = headerAccessor.getDestination();

        if(command.equals(StompCommand.SUBSCRIBE) && !destination.contains(NOTIFICATION)) {
            // 가족 채팅방 구독 시
            // TODO: Authentication - user & familyId (SubscriptionId 검증)
            // TODO: User 정보 검증 과정 수정 필요
            String userInfo = headerAccessor.getNativeHeader("id").get(0);
            String userId = userInfo.substring(0, userInfo.lastIndexOf("-"));
            Long familyId = Long.valueOf(userInfo.substring(userInfo.lastIndexOf("-") + 1));
            User user = userService.getUserById(userId);

            sessionService.subscribeFamily(user, familyId);
        } else if (command.equals(StompCommand.UNSUBSCRIBE)) {
            //구독 해제 시
            // TODO: 가족 채팅방 - noti 채널 구분 필요
            // TODO: Authentication - user & familyId (SubscriptionId 검증)
            // TODO: User 정보 검증 과정 수정 필요
            String userInfo = headerAccessor.getNativeHeader("id").get(0);
            String userId = userInfo.substring(0, userInfo.lastIndexOf("-"));
            Long familyId = Long.valueOf(userInfo.substring(userInfo.lastIndexOf("-") + 1));
            User user = userService.getUserById(userId);

            sessionService.unsubscribeFamily(user, familyId);
        }

        return message;
    }
}
