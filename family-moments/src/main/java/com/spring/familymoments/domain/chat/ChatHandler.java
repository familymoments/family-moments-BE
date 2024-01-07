package com.spring.familymoments.domain.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class ChatHandler extends TextWebSocketHandler {
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        //메세지 수신
        String input = message.getPayload();
        log.info(input);
        
        // 새로운 메세지 생성 및 발송
        TextMessage textMessage = new TextMessage("Hello, webSocket test");
        session.sendMessage(textMessage);
    }
}
