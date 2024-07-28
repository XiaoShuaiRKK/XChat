package com.xs.chat.service.listener;

import com.xs.entity.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class WebSocketEventListener {

    private final Set<String> connectedUsers = new HashSet<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event){
        log.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null){
            connectedUsers.remove(username);
            log.info("User Disconnected : " + username);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.ChatMessageType.LEAVE);
            chatMessage.setSender(username);
        }
    }

    public boolean isUsernameTaken(String username){
        return connectedUsers.contains(username);
    }

    public void addConnectedUser(String username){
        connectedUsers.add(username);
    }
}
