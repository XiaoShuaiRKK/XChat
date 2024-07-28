package com.xs.chat.controller;

import com.xs.chat.service.listener.WebSocketEventListener;
import com.xs.entity.chat.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class ChatMessageHandler {

    private final WebSocketEventListener webSocketEventListener;

    public ChatMessageHandler(WebSocketEventListener webSocketEventListener) {
        this.webSocketEventListener = webSocketEventListener;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage){
        chatMessage.setContent(HtmlUtils.htmlEscape(chatMessage.getContent()));
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
        String username = chatMessage.getSender();
        if(webSocketEventListener.isUsernameTaken(username)){
            chatMessage.setType(ChatMessage.ChatMessageType.LEAVE);
            chatMessage.setContent("Username already taken");
        }else {
            chatMessage.setType(ChatMessage.ChatMessageType.JOIN);
            chatMessage.setContent(HtmlUtils.htmlEscape(chatMessage.getSender()) + " joined the chat");
            headerAccessor.getSessionAttributes().put("username",username);
            webSocketEventListener.addConnectedUser(username);
        }
        return chatMessage;
    }

}
