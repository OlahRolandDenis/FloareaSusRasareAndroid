package com.example.testwifi3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    String IP_ADDRESS;

    View bgTransparentView;

    private Toolbar toolbar;
    private Button btnConnect;
    private ImageView btnRefresh;

    private LinearLayout paramsLayout;
    private CardView humidityCV, waterCV, oxygenCV, ledCV, ledControlCV, pumpControlCV;
    private Spinner spinnerPumps;

    private Slider ledSlider;

    private MaterialButton btnLedOn, btnLedOff;
    private EditText inputMilisWater;

    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    private UserSettings settings;

    SharedPreferences sharedPreferences;

     // private static  final String BASE_URL = "https://80.97.250.38:55443/oxygenie/test.php"; // global IP raspberry
    private static  final String BASE_URL = "https://192.168.4.210/oxygenie/test.php";   // local IP raspberry

    // private static  final String BASE_URL = "https://192.168.5.206:5501/index.html";
    // private static  final String urlsend = "https://android.taxi-ineu.ro/sendClient.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NukeSSLCerts.nuke();

        new ConnectionTask().execute();

        toolbar = (Toolbar) findViewById(R.id.myToolBar);
        setSupportActionBar( toolbar );

        sharedPreferences = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);

        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnRefresh = (ImageView) findViewById(R.id.btnRefresh);

        paramsLayout = (LinearLayout) findViewById(R.id.paramsLayout);

        ledCV = findViewById(R.id.ledCV);
        waterCV = findViewById(R.id.waterCV);

        spinnerPumps = findViewById(R.id.spinnerPumps);
        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(this, R.array.pumps, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerPumps.setAdapter(spinner_adapter);

        inputMilisWater = findViewById(R.id.inputMilisWater);

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

        pumpControlCV.findViewById(R.id.btnClosePumpsControl).setOnClickListener(v -> {
            pumpControlCV.setAlpha(0.0f);
            pumpControlCV.setVisibility(CardView.INVISIBLE);
            bgTransparentView.setAlpha(0.0f);
        });

        ((Button)findViewById(R.id.btnSendReq)).setOnClickListener(v->{
            new PostReq().execute();
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

    public static class NukeSSLCerts {
        protected static final String TAG = "NukeSSLCerts";

        public static void nuke() {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                                return myTrustedAnchors;
                            }

                            @Override
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            }
                        }
                };

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
    }//NukeSSLCerts

    class ConnectionTask extends AsyncTask<String, Void, Void> {

        private Exception exception;

        @SuppressLint("SetTextI18n")
        protected Void doInBackground(String... urls) {
            try {
                URL url = new URL(BASE_URL);

                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                int response_code = conn.getResponseCode();
                if ( response_code != 200 ) {
                    throw new RuntimeException("HttpResponseCode: " + response_code);
                } else {
                    System.out.println("connection secure!!!");

                    btnConnect.setText("DISCONNECT");

                    StringBuilder informationString  = new StringBuilder();
                    Scanner scanner = new Scanner(url.openStream());

                    while ( scanner.hasNext() ) {
                        informationString.append(scanner.nextLine());
                    }

                    scanner.close();

                    if ( informationString.charAt(0) != '[' ) {
                        System.out.println("not a correct stirng");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView)findViewById(R.id.tvStatus)).setText(
                                        "connected to server \n but cannot retrieve JSON :)"
                                );
                            }
                        });
                    } else {
                        JsonParser parse = new JsonParser();
                        JsonArray dataObject = (JsonArray) parse.parse(String.valueOf(informationString));

                        System.out.println("DATA OBJ: " + dataObject);
                        System.out.println("DATA OBJ LENGTH: " + dataObject.size());


                        // UPDATE THE UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for ( int i = 0; i < dataObject.size(); i++ ){
                                    JsonObject data = (JsonObject) dataObject.get(i);

                                    ((TextView)findViewById(R.id.tvStatus)).setText(
                                            ((TextView) findViewById(R.id.tvStatus)).getText() +
                                                    (data.get("id")).toString().replace("\"", "") + "  "
                                                    + (data.get("water_level")).toString().replace("\"", "")
                                                    + ": "
                                                    + (data.get("temperature")).toString().replace("\"", "")
                                                    + "\n"
                                    );
                                }
                            }
                        });
                    }
                }
            } catch (Exception e) {
                this.exception = e;
                e.printStackTrace();
            } finally {
                System.out.println("reached the end :D");
            }

            return null;
        }

        protected void onPostExecute() {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {
                URL url = new URL(BASE_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                // Send the request body
                String requestBody = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(requestBody);
                writer.flush();
                writer.close();

                // Get the response
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Process the response body
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(this.exception);
        }

    }
    class PostReq extends AsyncTask<String, Void, Void> {

        private Exception exception;

        @SuppressLint("SetTextI18n")
        protected Void doInBackground(String... urls) {
            try {
                String url = "https://80.97.250.38:55443/oxygenie/test2.php";
                URL object= new URL(url);

                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");

                JSONObject params   = new JSONObject();

                params.put("leds_intensity", 1);
                params.put("water_level", 1);
                params.put("temperature", 1);
                params.put("moist", 1);
                params.put("sunlight", 1);
                params.put("pump_1", 1);
                params.put("pump_2", 1);
                params.put("pump_3", 1);
                params.put("pump_4", 1);

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(params.toString());
                wr.flush();

                //display what returns the POST request
                StringBuilder sb = new StringBuilder();
                int HttpsResult = con.getResponseCode();
                if (HttpsResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "utf-8"));

                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    br.close();
                    System.out.println("" + sb.toString());
                } else {
                    System.out.println(con.getResponseMessage());
                }
            } catch (Exception e) {
                this.exception = e;
                e.printStackTrace();
            } finally {
                System.out.println("reached the end :D");
            }

            return null;
        }

        protected void onPostExecute() {
            // TODO: check this.exception
            // TODO: do something with the feed

            System.out.println(this.exception);
        }

    }


//    private class ConnectionTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            Log.d("ConnectionTask","doInBackground");
//            String response = null;
//
//            try {
//                String IP_ADDRESS = UserSettings.SELECTED_IP_ADDRESS;// get selected ip address ( from settings )
//                String IP_PORT = UserSettings.SELECTED_IP_PORT;
//
//                // connect to ip address and the socket "assigned" to it
//                socket = new Socket(IP_ADDRESS, 80);
//                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
//                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//                // give feedback that all is good
//                Log.d("ConnectionTask", "connected");
//                response = "ok";
//            } catch (Exception e) {
//                if ( IP_ADDRESS == "no ip address" || IP_ADDRESS == null )
//                    response = "NO_IP_ADDRESS";
//
//                Log.i( "TAG", e.toString());
//                e.printStackTrace();
//            }
//
//            return response;
//        }
//
//        @Override
//        protected void onPostExecute(String response) {
//            // Handle the response from the server here
//            if ( response == "NO_IP_ADDRESS" )
//                Toast.makeText(
//                        MainActivity.this,
//                        "PLEASE SELECT AN IP ADDRESS!",
//                        Toast.LENGTH_SHORT
//                ).show();
//            else {
//                paramsLayout.setVisibility(LinearLayout.VISIBLE);
//                paramsLayout.animate().alpha(1.0f);
//                btnRefresh.setVisibility(ImageButton.VISIBLE);
//
//                if (response == null) {
//                    Toast.makeText(
//                            MainActivity.this,
//                            "Could not connect to device 😞",
//                            Toast.LENGTH_SHORT
//                    ).show();
//
//
//                    settings.setIs_connected_to_device(true);
//
//                    Log.d("ConnectionTask", "could not connect to device");
//                    btnConnect.setText("Connect");
//                } else {
//                    Toast.makeText(
//                            MainActivity.this,
//                            "Connected to device! 😄",
//                            Toast.LENGTH_SHORT
//                    ).show();
//
//                    Log.d("ConnectionTask", "Connected to device");
//                    btnConnect.setText("Disconect");
//                }
//            }
//
//        }
//    }

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
                System.out.println("RESPONSE SENZORI: " + response);

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