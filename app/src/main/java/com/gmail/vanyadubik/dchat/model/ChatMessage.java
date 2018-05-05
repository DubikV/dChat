package com.gmail.vanyadubik.dchat.model;

public class ChatMessage {
    private String user;
    private String message;
    private boolean own;

    public ChatMessage(String user, String message, boolean own) {
        this.user = user;
        this.message = message;
        this.own = own;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isOwn() {
        return own;
    }

    public void setOwn(boolean own) {
        this.own = own;
    }
}
