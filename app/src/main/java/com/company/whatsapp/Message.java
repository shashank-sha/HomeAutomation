package com.company.whatsapp;

/**
 * Created by sudhanshu on 5/11/15.
 */
public class Message {
    private String fromName, message;


    public Message() {
    }

    @Override
    public String toString() {
        return this.fromName +" : " + this.message;
    }

    public Message(String fromName, String message) {
        this.fromName = fromName;
        this.message = message;

    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
