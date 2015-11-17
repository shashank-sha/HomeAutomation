package com.company.whatsapp;

/**
 * Created by sudhanshu on 3/11/15.
 */
public class ChatMessage  {
    private String fromName;
    private String toName;
    private  String message;


    public ChatMessage(String toName, String message, String fromName) {
        super();
        this.toName = toName;
        this.message = message;
        this.fromName = fromName;

    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
        //return super.toString();
    }



}
