package com.example.testwifi3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
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

import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.w3c.dom.Text;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import kotlinx.coroutines.MainCoroutineDispatcher;

public class MainActivity extends AppCompatActivity {

    String IP_ADDRESS;
    private Button btnOn, btnOff, btnConnect, btnGetInfo;
    private TextView params_text_view;

    private CardView humidityCV, waterCV, oxygenCV;

    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    private UserSettings settings;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);

        btnOn = (Button) findViewById(R.id.btn_on);
        btnOff = (Button) findViewById(R.id.btn_off);
        btnGetInfo = ( Button ) findViewById(R.id.btn_getInfo);
        btnConnect = (Button) findViewById(R.id.btn_connect);

        params_text_view = (TextView) findViewById(R.id.paramsTextView);

        settings = ( UserSettings ) getApplication();
        IP_ADDRESS = settings.getIPAddress();

        btnConnect.setOnClickListener(v -> {
            if ( socket == null ) {
                connectToDevice();
            } else {
                disconnectFromDevice();
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

        btnGetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v  ) {
                Log.d("SETTING FROM MAIN", " MiAI IP: " + settings.getIPAddress());
                new SendCommandTask().execute("sayHi");
                Log.i("COMMAND_TASK", "trimis somanda pt sayHi :D");
            }
        });

        Set<String> ipAddressesSet = sharedPreferences.getStringSet("ALL_IP_ADDRESSES", null);
        System.out.println("ipAddressesSet is: " + ipAddressesSet);

        loadSharedPreferences();
    }


    private void loadSharedPreferences() {

        String ip_address = sharedPreferences.getString(UserSettings.SELECTED_IP_ADDRESS, "no ip address");

        settings.setIPAddress(ip_address);
        IP_ADDRESS = ip_address;

        Set<String> news = new HashSet<String>();
        news = sharedPreferences.getStringSet("ALL_IP_ADDRESSES", null);
        System.out.println(news);

        updateView();
    }

    public void updateView() {
        TextView ipAddressTextView = ( TextView ) findViewById(R.id.currentIPAddressTextView);
        ipAddressTextView.setText(settings.getIPAddress());
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

    public void navigateToSettings( View view ) {
        Log.d("NAVIGATE", "click to navigate to settings :D");

        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private class ConnectionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.d("ConnectionTask","doInBackground");
            String response = null;

            try {
                String IP_ADDRESS = UserSettings.SELECTED_IP_ADDRESS;// get selected ip address ( from settings )
                String IP_PORT = UserSettings.SELECTED_IP_PORT;

                // connect to ip address and the socket "assigned" to it
                socket = new Socket(IP_ADDRESS, Integer.parseInt(IP_PORT));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // give feedback that all is good
                Log.d("ConnectionTask", "connected");
                response = "ok";
            } catch (Exception e) {
                if ( IP_ADDRESS == "no ip address" || IP_ADDRESS == null )
                    response = "NO_IP_ADDRESS";

                Log.i( "TAG", e.toString());
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            // Handle the response from the server here
            if ( response == "NO_IP_ADDRESS" )
                Toast.makeText(
                        MainActivity.this,
                        "PLEASE SELECT AN IP ADDRESS!",
                        Toast.LENGTH_SHORT
                ).show();
            else
            if (response == null) {
                Toast.makeText(
                        MainActivity.this,
                        "Could not connect to device 😞",
                        Toast.LENGTH_SHORT
                ).show();

                Log.d("ConnectionTask","could not connect to device");
                btnConnect.setText("Connect");
            } else {
                Toast.makeText(
                        MainActivity.this,
                        "Connected to device! 😄",
                        Toast.LENGTH_SHORT
                ).show();

                Log.d("ConnectionTask","Connected to device");
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

            try {
                String message = params[0];
                out.println(message);

                if ( message != "1" && message != "0")
                    params_text_view.setText("waiting...");

                response = in.readLine();   // gets only the first line of the message that is sent

                String[] parts = response.split("   ");
                Log.d("STRING_PARTS", "Logging parts: ");

                // display the sent data on the screen
                if ( parts.length > 1 ) {
                    params_text_view.setText("");
                    for (int index = 0; index < parts.length - 1; index++) {
                        Log.d("STRING_PARTS", parts[index]);
                        params_text_view.setText(params_text_view.getText() + "\n" + parts[index]);
                    }
                }

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