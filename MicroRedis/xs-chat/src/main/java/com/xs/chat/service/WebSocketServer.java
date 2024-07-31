package com.xs.chat.service;

import java.util.HashSet;
import java.util.Set;

public class WebSocketServer {
    private final int id;
    private final String name;
    private final Set<String> connectedUsers = new HashSet<>();

    public WebSocketServer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean isUsernameTaken(String username){
        return connectedUsers.contains(username);
    }

    public void addConnectedUser(String username){
        connectedUsers.add(username);
    }

    public void removeUser(String username){
        connectedUsers.remove(username);
    }

    public int getId() {
        return id;
    }
}
