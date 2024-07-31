package com.xs.chat.service.listener;

import com.xs.chat.service.WebSocketServer;
import com.xs.entity.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.*;

@Component
@Slf4j
public class WebSocketEventListener {

    private static final Map<String,WebSocketServer> servers = new HashMap<>();
    private static int serverId = 1;

    static {
        servers.put("public",new WebSocketServer(serverId,"public"));
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event){
        log.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String serverName = (String) headerAccessor.getSessionAttributes().get("serverName");
        if (username != null && serverName != null){
            servers.get(serverName).removeUser(username);
            log.info("User Disconnected : " + username);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.ChatMessageType.LEAVE);
            chatMessage.setSender(username);
        }
    }

    public boolean isUsernameTaken(String serverName,String name){
        return servers.get(serverName).isUsernameTaken(name);
    }

    public void addConnectedUser(String serverName,String username){
        servers.get(serverName).addConnectedUser(username);
    }

    public void addChannel(String serverName){
        servers.put(serverName,new WebSocketServer(serverId++,serverName));
    }

    public boolean checkChannel(String serverName){
        return servers.containsKey(serverName);
    }
}
