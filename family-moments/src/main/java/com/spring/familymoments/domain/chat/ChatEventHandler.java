package com.spring.familymoments.domain.chat;

import com.spring.familymoments.domain.redis.RedisService;
import com.spring.familymoments.domain.user.UserService;
import com.spring.familymoments.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class ChatEventHandler {
    private final UserService userService; // TODO: 임시, 차후 삭제
    private final SessionService sessionService;

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap((Message<?>) accessor.getHeader("simpConnectMessage"));

        // TODO: 유저 & 세션 정보 확인, Authentication 필요
        String sessionId = headerAccessor.getSessionId();
        String userId = headerAccessor.getNativeHeader("id").get(0);
        User user = userService.getUserById(userId);

        sessionService.connect(sessionId, user);
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        // TODO: sessionId : userId 확인

        // TODO: userID를 바탕으로 connect or unsub 중인 내역이 있다면 offline으로 변경
        // TODO: sessionId:userID 삭제
    }
}
