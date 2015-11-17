package com.company.whatsapp;

/**
 * Created by sudhanshu on 5/11/15.
 */
public class Message {
    private String fromName, message,toName;
    



    public Message() {
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    @Override
    public String toString() {
        return this.message;
    }

    public Message(String fromName, String message,String toName) {
        this.fromName = fromName;
        this.message = message;
        this.toName = toName;


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
