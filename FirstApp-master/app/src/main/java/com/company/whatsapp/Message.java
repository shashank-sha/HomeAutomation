package com.company.whatsapp;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by sudhanshu on 5/11/15.
 */
public class Message implements Comparable<Message> {
    private String fromName, message,toName;
    private Date dateTime;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
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

    public Message(String fromName, String message,String toName,Date dateTime,String url) {
        this.fromName = fromName;
        this.message = message;
        this.toName = toName;
        this.dateTime = dateTime;
        this.url = url;


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

    private String getDaysAgo(Date date){
        long days = (new Date().getTime() - date.getTime()) / 86400000;

        if(days == 0) return "Today";
        else if(days == 1) return "Yesterday";
        else return days + " days ago";
    }






}
