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

import android.widget.TextClock;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.w3c.dom.Text;

import java.net.URI;
import java.net.URISyntaxException;
public class MainActivity extends AppCompatActivity {
    private static final String SERVER_IP = "192.168.52.177"; // Replace with the IP address of your Raspberry Pi Pico board
    private static final int SERVER_PORT = 80; // Replace with the port number used by the Python code

    private Button btnOn, btnOff, btnConnect, btnSayHi;
    private TextView lbl_connect, params_text_view;

    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOn = (Button) findViewById(R.id.btn_on);
        btnOff = (Button) findViewById(R.id.btn_off);
        btnSayHi = ( Button ) findViewById(R.id.btn_sayHi);
        btnConnect = (Button) findViewById(R.id.btn_connect);
        lbl_connect = (TextView) findViewById(R.id.lbl_connect);

        params_text_view = (TextView) findViewById(R.id.paramsTextView);

        btnConnect.setOnClickListener(v -> {
            if ( socket == null ) {
                connectToDevice();
            } else {
                disconnectFromDevice();
                lbl_connect.setText("Disconnected");
                btnConnect.setText("Connect");
            }
        });

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendCommandTask().execute("1");
                Log.i( "TAG"," trimis 1");
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendCommandTask().execute("0");
                Log.i( "TAG"," trimis 0");
            }
        });

        btnSayHi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v  ) {
                new SendCommandTask().execute("sayHi");
                Log.i("COMMAND_TASK", "trimis somanda pt sayHi :D");
            }
        });
    }

    private void disconnectFromDevice() {
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

        socket = null;
        out = null;
        in = null;
    }

    private void connectToDevice() {
        new ConnectionTask().execute();
        Log.i( "ConnectionTask"," pornire connectare la Device");
    }

    private class ConnectionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.d("ConnectionTask","doInBackground");
            String response = null;
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Log.d("ConnectionTask", "connected");

                response = "ok";

            } catch (Exception e) {
                Log.i( "TAG",e.toString());
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            // Handle the response from the server here
            if (response == null) {
                Log.d("ConnectionTask","could not connect to device");
                lbl_connect.setText("Could not connect to device");
                btnConnect.setText("Connect");
            } else {
                Log.d("ConnectionTask","Connected to device");
                lbl_connect.setText("Connected");
                btnConnect.setText("Disconect");
            }

        }
    }

    private class SendCommandTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d("TAG","doInBackground");
            if ( socket == null || out == null || in == null ){
                Log.d("SendCommandTask", "cannot send command if we are not connected");
                return null;
            }

            String response = null;
            String resposes[] = {};

            try {
                String message = params[0];
                out.println(message);
                response = in.readLine();
                params_text_view.setText(response);
                Log.i( "RESPONSE_TAG",response);
            } catch (Exception e) {
                Log.i( "ERROR_TAG", e.toString());
                e.printStackTrace();
                disconnectFromDevice();
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