package com.spring.familymoments.domain.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class ChatHandler extends TextWebSocketHandler {
    private final Set<WebSocketSession> sessions = new HashSet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("{} 연결됨", session.getId());
        session.sendMessage(new TextMessage("Connected!"));
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //메세지 수신
        String input = message.getPayload();
        log.info(input);

        // 새로운 메세지 생성 및 발송
        TextMessage textMessage = new TextMessage(input);
//        session.sendMessage(textMessage);
        sendToEachSocket(sessions, session, textMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} 연결 끊김", session.getId());
        sessions.remove(session);
    }

    private void sendToEachSocket(Set<WebSocketSession> sessions, WebSocketSession sender,TextMessage message) {
        sessions.parallelStream()
                .filter(session -> !session.equals(sender))
                .forEach( session -> {
                    try {
                        session.sendMessage(message);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
