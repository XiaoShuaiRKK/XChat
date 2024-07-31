package com.xs.chat.controller;

import com.xs.chat.service.listener.WebSocketEventListener;
import com.xs.entity.chat.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@RestController
public class ChatMessageServerHandler {

    private final WebSocketEventListener webSocketEventListener;
    public ChatMessageServerHandler(WebSocketEventListener webSocketEventListener) {
        this.webSocketEventListener = webSocketEventListener;
    }

    @MessageMapping("/chat.sender")
    @SendTo("/topic/{channel}")
    public ChatMessage senderMessage(ChatMessage chatMessage){
        chatMessage.setContent(HtmlUtils.htmlEscape(chatMessage.getContent()));
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/{channel}")
    public ChatMessage addUser(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
        String username = chatMessage.getSender();
        String serverName = chatMessage.getServerName();
        ChatMessage checkChat = createChannel(chatMessage,headerAccessor);
        if (checkChat.getType() == ChatMessage.ChatMessageType.LEAVE)
            return chatMessage;
        if(webSocketEventListener.isUsernameTaken(serverName,username)){
            chatMessage.setType(ChatMessage.ChatMessageType.LEAVE);
            chatMessage.setContent("Username already taken");
        }else {
            chatMessage.setType(ChatMessage.ChatMessageType.JOIN);
            chatMessage.setContent(HtmlUtils.htmlEscape(chatMessage.getSender()) + " joined the chat");
            headerAccessor.getSessionAttributes().put("username",username);
            webSocketEventListener.addConnectedUser(serverName,username);
        }
        return chatMessage;
    }

    @MessageMapping("/chat.create")
    @SendTo("/topic/{channel}")
    public ChatMessage createChannel(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
        String serverName = chatMessage.getServerName();
        if (!webSocketEventListener.checkChannel(serverName)){
            webSocketEventListener.addChannel(serverName);
        }
        chatMessage.setType(ChatMessage.ChatMessageType.JOIN);
        chatMessage.setContent("Created channel successful");
        return chatMessage;
    }


}
