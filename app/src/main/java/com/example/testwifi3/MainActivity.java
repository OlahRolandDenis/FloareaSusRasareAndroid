package com.example.testwifi3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.helper.widget.Carousel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.AsyncTask;


import android.os.Debug;
import android.transition.Slide;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

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

    View bgTransparentView;

    private Toolbar toolbar;
    private Button btnConnect;
    private ImageButton btnRefresh;

    private LinearLayout paramsLayout;
    private CardView humidityCV, waterCV, oxygenCV, ledCV, ledControlCV, pumpControlCV;

    private Slider ledSlider;

    private MaterialButton btnLedOn, btnLedOff;

    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    private UserSettings settings;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.myToolBar);
        setSupportActionBar( toolbar );

        sharedPreferences = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);

        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnRefresh = (ImageButton) findViewById(R.id.btnRefresh);

        paramsLayout = (LinearLayout) findViewById(R.id.paramsLayout);

        ledCV = findViewById(R.id.ledCV);
        waterCV = findViewById(R.id.waterCV);

        bgTransparentView = (View) findViewById(R.id.bgTransparentView);

        ledControlCV = (CardView) findViewById(R.id.ledControlCV);
        pumpControlCV = (CardView) findViewById(R.id.pumpsControlCV);

        btnLedOn = findViewById(R.id.btnLedON);
        btnLedOff = findViewById(R.id.btnLedOFF);
        ledSlider = findViewById(R.id.ledSlider);

        settings = ( UserSettings ) getApplication();
        IP_ADDRESS = settings.getIPAddress();



        if ( settings.isIs_connected_to_device() ) {
            paramsLayout.setVisibility(LinearLayout.VISIBLE);
            paramsLayout.animate().alpha(1.0f);
            btnRefresh.setVisibility(ImageButton.VISIBLE);
        }


        btnConnect.setOnClickListener(v -> {
            if ( socket == null ) {
                connectToDevice();
            } else {
                disconnectFromDevice();
                btnConnect.setText("Connect");
            }
        });

        ledCV.setOnClickListener(v->{
            bgTransparentView.setAlpha(0.5f);
            ledControlCV.setVisibility(CardView.VISIBLE);
            ledControlCV.setAlpha(1.0f);
        });

        btnLedOn.setOnClickListener(v -> {
            new SendCommandTask().execute("LED_ON");
        });

        btnLedOff.setOnClickListener(v -> {
            new SendCommandTask().execute("LED_OFF");
        });

        ledSlider.addOnChangeListener((slider, value, fromUser) -> {
            new SendCommandTask().execute("*" + value);
        });

        ledControlCV.findViewById(R.id.btnCloseLedControl).setOnClickListener(v-> {
            ledControlCV.setAlpha(0.0f);
            ledControlCV.setVisibility(CardView.INVISIBLE);
            bgTransparentView.setAlpha(0.0f);
        });

        waterCV.setOnClickListener(v -> {
            pumpControlCV.setVisibility(CardView.VISIBLE);
            pumpControlCV.setAlpha(1.0f);
            bgTransparentView.setAlpha(0.5f);
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
        ipAddressTextView.setText("IP: " + settings.getIPAddress());
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
                socket = new Socket(IP_ADDRESS, 80);
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
            else {
                paramsLayout.setVisibility(LinearLayout.VISIBLE);
                paramsLayout.animate().alpha(1.0f);
                btnRefresh.setVisibility(ImageButton.VISIBLE);

                if (response == null) {
                    Toast.makeText(
                            MainActivity.this,
                            "Could not connect to device 😞",
                            Toast.LENGTH_SHORT
                    ).show();


                    settings.setIs_connected_to_device(true);

                    Log.d("ConnectionTask", "could not connect to device");
                    btnConnect.setText("Connect");
                } else {
                    Toast.makeText(
                            MainActivity.this,
                            "Connected to device! 😄",
                            Toast.LENGTH_SHORT
                    ).show();

                    Log.d("ConnectionTask", "Connected to device");
                    btnConnect.setText("Disconect");
                }
            }

        }
    }

    public class SendCommandTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d("TAG","doInBackground");

            if ( params[0].charAt(0) == '*' ) {
                System.out.println("you want to change the intensity of the led");
            }
            if ( socket == null || out == null || in == null ){
                Log.d("SendCommandTask", "cannot send command if we are not connected");
                return null;
            }

            String response = null;

            try {
                String message = params[0];
                out.println(message);

                if ( message == "LED_ON" ) {
                    System.out.println("SENT MESSAGE TO TURN ON THE LED");
                }

                if ( message == "LED_OFF") {
                    System.out.println("SENT MESSAGE TO TURN OFF THE LED");
                }

                if ( message.charAt(0) == '*' ) {
                    System.out.println("you want to change the intensity of the led");
                }

                response = in.readLine();   // gets only the first line of the message that is sent

                String[] parts = response.split("   ");
                Log.d("STRING_PARTS", "Logging parts: ");

                // display the sent data on the screen
                if ( parts.length > 1 ) {
                    for (int index = 0; index < parts.length - 1; index++) {
                        Log.d("STRING_PARTS", parts[index]);
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