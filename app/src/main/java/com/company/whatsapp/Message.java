package com.company.whatsapp;

import java.util.Comparator;

/**
 * Created by sudhanshu on 5/11/15.
 */
public class Message implements Comparable<Message> {
    private String fromName, message,toName;
    private String dateTime;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

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
        return this.message ;
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

    @Override
    public int compareTo(Message another) {
        return getDateTime().compareTo(another.getDateTime());
    }
}
