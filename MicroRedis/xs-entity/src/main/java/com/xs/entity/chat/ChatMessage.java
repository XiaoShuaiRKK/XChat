package com.xs.entity.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String content;
    private String sender;
    private ChatMessageType type;
    public enum ChatMessageType{
        CHAT,JOIN,LEAVE
    }
}
