package com.gmail.vanyadubik.dchat.model;

import java.util.Date;

public class ChatMessage {
    private String user;
    private String message;
    private boolean own;
    private boolean server;
    private Date date;

    public ChatMessage(String user, String message, boolean own, boolean server, Date date) {
        this.user = user;
        this.message = message;
        this.own = own;
        this.server = server;
        this.date = date;
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

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
