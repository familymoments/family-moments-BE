package com.spring.familymoments.domain.chat;

import com.spring.familymoments.domain.user.UserService;
import com.spring.familymoments.domain.user.entity.User;
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
        // TODO: User 정보 검증 과정 수정 필요
        String userId = headerAccessor.getNativeHeader("id").get(0);
        User user = userService.getUserById(userId);

        sessionService.connect(sessionId, user);
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap((Message<?>) accessor.getHeader("simpConnectMessage"));

        String sessionId = headerAccessor.getSessionId();

        sessionService.disconnect(sessionId);
    }
}
