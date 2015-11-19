package com.company.myapplication;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Created by sudhanshu on 3/11/15.
 */
public class Server {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
    private static String message;
    private static String TAG = "server";

    public static void main(String[] args) {

        try {
            serverSocket = new ServerSocket(9000);  //Server socket
        } catch (IOException e) {
            Log.d(TAG,"Could not listen on port: 9000");
        }

        Log.d(TAG, "Server started. Listening to the port 4444");

        while (true) {
            try {
                clientSocket = serverSocket.accept();   //accept the client connection
                inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader); //get the client message
                message = bufferedReader.readLine();

                System.out.println(message);
                inputStreamReader.close();
                clientSocket.close();

            } catch (IOException ex) {
                Log.d(TAG,"Problem in message reading");
            }
        }

    }
}
