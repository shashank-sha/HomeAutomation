package com.company.socketapplication.com.company.SocketApplication.comm;

import android.util.Log;

import com.company.socketapplication.interfacer.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
//import java.net.Socket;
import java.util.HashMap;
import java.io.DataInputStream;
import java.util.Iterator;

/**
 * Created by sudhanshu on 1/11/15.
 */
public class Socketer implements com.company.socketapplication.interfacer.Socket {

    private static final String AUTHENTICATION_SERVER_ADDRESS = "http://localhost:9000/Chat";

    private int listeningPort = 0;

    private static final String HTTP_REQUEST_FAILED = null;

    private HashMap<InetAddress, java.net.Socket> sockets = new HashMap<InetAddress, java.net.Socket>();

    private ServerSocket serverSocket = null;

    private boolean listening;

    private class RecieveConnection extends Thread {

        java.net.Socket clientSocket = null;


        public RecieveConnection(com.company.socketapplication.interfacer.Socket socket) {
            this.clientSocket = (java.net.Socket) socket;
            Socketer.this.sockets.put( serverSocket.getInetAddress(), (java.net.Socket) socket);
       //     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


        }

        @Override
        public void run() {

            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine = "";
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.equals("exit") == false) {

                    } else {
                        clientSocket.shutdownInput();
                        clientSocket.shutdownOutput();
                        clientSocket.close();
                        Socketer.this.sockets.remove(((ServerSocket) serverSocket).getInetAddress());
                    }
                }
            } catch (IOException e) {
                Log.e("RecieveConnection.run:", "when recieving connection");
            }
        }
    }


    @Override
    public String sendHTTPRequest(String params) {
        URL url;
        String result = new String();
        try {
            url = new URL(AUTHENTICATION_SERVER_ADDRESS);
            HttpURLConnection connection;
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            PrintWriter out = new PrintWriter(connection.getOutputStream());

            out.println(params);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                result = result.concat(inputLine);
            }

            in.close();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(result.length()==0)
            return HTTP_REQUEST_FAILED;

        return result;
    }

    @Override
    public int startListeningPort(int portNo) {
        listening =true;
        try{
            serverSocket = new ServerSocket(portNo);
            this.listeningPort = portNo;

        } catch (IOException e) {
            return 0;
        }
        while (listening) {
            try {
                new RecieveConnection((Socket) serverSocket.accept()).start();
            } catch (IOException e) {
                return 2;
            }
        }
        try {
            serverSocket.close();
            } catch (IOException e) {
            Log.e("Exception server socket", "Exception in closong server socket");
            return 3;
        }

        return 1;

    }

    public void exit(){
        for(Iterator<java.net.Socket> iterator = sockets.values().iterator();iterator.hasNext();)
        {
          java.net.Socket socket =iterator.next();
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            }catch (Exception e){

            }
        }

    }

}

