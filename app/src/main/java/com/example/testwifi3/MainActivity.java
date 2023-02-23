package com.example.testwifi3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.AsyncTask;


import android.os.Debug;
import android.view.View;
import android.widget.Button;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;

import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
public class MainActivity extends AppCompatActivity {
    private static final String SERVER_IP = "192.168.17.177"; // Replace with the IP address of your Raspberry Pi Pico board
    private static final int SERVER_PORT = 80; // Replace with the port number used by the Python code

    private Button btnOn, btnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOn = (Button) findViewById(R.id.btn_on);
        btnOff = (Button) findViewById(R.id.btn_off);

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LedControlTask().execute("1");
                Log.i( "TAG"," trimis 1");
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LedControlTask().execute("0");
                Log.i( "TAG"," trimis 0");
            }
        });
    }

    private class LedControlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d("TAG","doInBackground");
            Socket socket = null;
            PrintWriter out = null;
            BufferedReader in = null;
            String response = null;
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String message = params[0];
                out.println(message);
                response = in.readLine();
                Log.i( "TAG",response);

            } catch (Exception e) {
                Log.i( "TAG",e.toString());
                e.printStackTrace();
            } finally {
                if ( in != null ) {
                    try {
                        in.close();
                    } catch (IOException e) {

                    }
                }
                if ( out != null ) {
                    out.close();
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {

                    }
                }
            }

            Log.d("TAG","sent the data");
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            // Handle the response from the server here
            if (response != null) {
                Log.i("TAG", "Response from server: " + response);
            } else {
                Log.i("TAG", "Failed to get response from server");
            }

        }
    }
}