package com.company.whatsapp;

/**
 * Created by sudhanshu on 3/11/15.
 */
public class ChatMessage  {
    private String toName;
    private  String message;


    public ChatMessage(String toName, String message) {
        super();
        this.toName = toName;
        this.message = message;

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
