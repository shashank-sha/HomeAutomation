package com.company.socketapplication.interfacer;

/**
 * Created by sudhanshu on 1/11/15.
 */
public interface Socket {

    public String sendHTTPRequest(String Params);
    public int startListeningPort(int Port);

//    public void stopListening();
//    public void exit();

}
