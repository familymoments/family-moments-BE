package com.spring.familymoments.domain.chat;

import com.spring.familymoments.domain.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {
    private final RedisService redisService;

    private static final String PREFIX_USER_ID = "UI:";
    private static final String PREFIX_SESSION_ID = "SI:";
    private static final String PREFIX_FAMILY_ID = "FM";


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        StompCommand command = headerAccessor.getCommand();

        if(command.equals(StompCommand.SUBSCRIBE)) {
            String destination = headerAccessor.getDestination();

            if(destination.substring(destination.lastIndexOf("/")).equals("/notification")) {
                String userId = destination.replace("/sub/", "").replace("/notification", "");
                String sessionId = headerAccessor.getSessionId();

                redisService.setValues(PREFIX_USER_ID + userId, sessionId);
                redisService.setValues(PREFIX_SESSION_ID + sessionId, userId);
            }
        } else if(command.equals(StompCommand.DISCONNECT)) {
            String sessionId = headerAccessor.getSessionId();
            String userId = String.valueOf(redisService.getValues(PREFIX_SESSION_ID + sessionId));

            // user의 가족 목록 load, redis에서 connect 여부 확인

            // disconnect 시 session 정보 삭제
            redisService.deleteValues(PREFIX_SESSION_ID + sessionId);
            redisService.deleteValues(PREFIX_USER_ID + userId);
        }

        return message;
    }
}
