package com.company.myapplication;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    static String TAG = "ClientToServer";
    private Socket client;
    private PrintWriter printwriter;
    private EditText textField;
    private Button button;
    private String messsage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textField = (EditText) findViewById(R.id.editText1); //reference to the text field
        button = (Button) findViewById(R.id.button1);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                messsage = textField.getText().toString(); //get the text message on the text field
                textField.setText("");//Reset the text field to blank

                new ServerTask().execute();
            }
    });
    }

        public class ServerTask extends AsyncTask<Void,Void,Void> {
            @Override
            protected Void doInBackground(Void... params) {


                try {

                    client = new Socket("10.0.2.2", 9000);  //connect to server

                    printwriter = new PrintWriter(client.getOutputStream(), true);
                    printwriter.write(messsage);  //write the message to output stream

                    printwriter.flush();
                    printwriter.close();
                    client.close();   //closing the connection

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


        }
