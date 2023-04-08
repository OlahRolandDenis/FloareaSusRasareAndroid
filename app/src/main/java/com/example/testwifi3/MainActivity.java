package com.example.testwifi3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.slider.Slider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {

    private UserSettings settings;

    private static final String URL_LOCAL = "https://192.168.4.210";
    private static final String URL_GLOBAL = "https://80.97.250.38:55443";

    private static final String URL_GET_PLANT_DATA = "/oxygenie/get_plant_data.php";
    private static final String URL_SEND_COMMAND = "/oxygenie/send_command.php";

    private LinearLayout paramsLayout;
    ArrayList<TextView> paramsValuesViewsList = new ArrayList<>();
    String[] params_db = {
            "leds_intensity",
            "water_level",
            "temperature",
            "moist",
            "sunlight",
            "pump_1",
            "pump_2",
            "pump_3",
            "pump_4"
    };

    JsonObject plant_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NukeSSLCerts.nuke();

        new ConnectionTask().execute(); // connect to server

        paramsLayout = (LinearLayout) findViewById(R.id.paramsLayout);

        paramsValuesViewsList.add(findViewById(R.id.textLEDValue));
        paramsValuesViewsList.add(findViewById(R.id.textWaterValue));
        paramsValuesViewsList.add(findViewById(R.id.textTemperatureValue));
        paramsValuesViewsList.add(findViewById(R.id.textMoistValue));
        paramsValuesViewsList.add(findViewById(R.id.textSunlightValue));
        paramsValuesViewsList.add(findViewById(R.id.textPUMP1Value));
        paramsValuesViewsList.add(findViewById(R.id.textPUMP2Value));
        paramsValuesViewsList.add(findViewById(R.id.textPUMP3Value));
        paramsValuesViewsList.add(findViewById(R.id.textPUMP4Value));

        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(this, R.array.pumps, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        ((Spinner)findViewById(R.id.spinnerPumps)).setAdapter(spinner_adapter);

        settings = ( UserSettings ) getApplication();

        ((ImageView) findViewById(R.id.btnRefresh)).setOnClickListener(v -> {
            new GetReq().execute();
        });

        findViewById(R.id.ledCV).setOnClickListener( v-> {
            findViewById(R.id.bgTransparentView).setAlpha(0.5f);
            findViewById(R.id.ledControlCV).setVisibility(CardView.VISIBLE);
            findViewById(R.id.ledControlCV).setAlpha(1.0f);
        });

        findViewById(R.id.btnLedON).setOnClickListener(v -> {
           // new SendCommandTask().execute("LED_ON");
        });

        findViewById(R.id.btnLedON).setOnClickListener(v -> {
           // new SendCommandTask().execute("LED_OFF");
        });

        ((Slider)findViewById(R.id.ledSlider)).addOnChangeListener((slider, value, fromUser) -> {
            // new SendCommandTask().execute("*" + value);
        });

        findViewById(R.id.ledControlCV).findViewById(R.id.btnCloseLedControl).setOnClickListener(v-> {
            findViewById(R.id.ledControlCV).setAlpha(0.0f);
            findViewById(R.id.ledControlCV).setVisibility(CardView.INVISIBLE);
            findViewById(R.id.bgTransparentView).setAlpha(0.0f);
        });

        findViewById(R.id.waterCV).setOnClickListener(v -> {
            findViewById(R.id.pumpsControlCV).setVisibility(CardView.VISIBLE);
            findViewById(R.id.pumpsControlCV).setAlpha(1.0f);
            findViewById(R.id.bgTransparentView).setAlpha(0.5f);
        });

        findViewById(R.id.pumpsControlCV).findViewById(R.id.btnClosePumpsControl).setOnClickListener(v -> {
            findViewById(R.id.pumpsControlCV).setAlpha(0.0f);
            findViewById(R.id.pumpsControlCV).setVisibility(CardView.INVISIBLE);
            findViewById(R.id.bgTransparentView).setAlpha(0.0f);
        });

    }

    public void navigateToSettings( View view ) {
        Log.d("NAVIGATE", "click to navigate to settings :D");
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void loadSpinner() {
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        paramsLayout.setVisibility(View.INVISIBLE);
    }

    private void hideSpinner() {
        paramsLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.progressbar).setVisibility(View.GONE);
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
                URL url = new URL(URL_GLOBAL + URL_GET_PLANT_DATA );

                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                int response_code = conn.getResponseCode();
                if ( response_code != 200 ) {

                    System.out.println("not 200 upsi");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Could not connect to server:(",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });

                    throw new RuntimeException("HttpResponseCode: " + response_code);
                } else {
                    System.out.println("connection secure!!!");

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(
                                    MainActivity.this,
                                    "connected to server <3",
                                    Toast.LENGTH_SHORT
                            ).show();

                            paramsLayout.setVisibility(View.VISIBLE);
                            paramsLayout.setAlpha(1.0f);

                            findViewById(R.id.btnRefresh).setVisibility(View.VISIBLE);
                        }
                    });

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
//                                ((TextView)findViewById(R.id.tvStatus)).setText(
//                                        "connected to server \n but cannot retrieve JSON :)"
//                                );
                            }
                        });
                    } else {
                        JsonParser parse = new JsonParser();
                        JsonArray dataObject = (JsonArray) parse.parse(String.valueOf(informationString));

                        System.out.println("DATA OBJ: " + dataObject);
                        System.out.println("DATA OBJ LENGTH: " + dataObject.size());
                        System.out.println("FIRST ELM: " + dataObject.get(0));

                        plant_data = (JsonObject) dataObject.get(0);

                        // UPDATE THE UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for ( int i = 0; i < params_db.length; i++ ){
                                    ((TextView) paramsValuesViewsList.get(i))
                                            .setText((plant_data.get(params_db[i])).toString().replace("\"", ""));
                                }
                            }
                        });
                    }
                }
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                MainActivity.this,
                                "Could not connect to server:(",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
                this.exception = e;
                e.printStackTrace();
            } finally {
                System.out.println("reached the end :D");
            }

            return null;
        }

        protected void onPostExecute(Result result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            System.out.println(this.exception);
        }

    }

    class GetReq extends AsyncTask<String, Void, Void> {

        private Exception exception;
        @SuppressLint("SetTextI18n")
        protected Void doInBackground(String... urls) {
            try {
                URL url = new URL(URL_GLOBAL + URL_GET_PLANT_DATA );

                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                int response_code = conn.getResponseCode();
                if ( response_code != 200 ) {

                    System.out.println("not 200 upsi");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Could not connect to server:(",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });

                    throw new RuntimeException("HttpResponseCode: " + response_code);
                } else {
                    System.out.println("got the DATA <3 !!!");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(
                                    MainActivity.this,
                                    "got the data <3",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });

                    StringBuilder informationString  = new StringBuilder();
                    Scanner scanner = new Scanner(url.openStream());

                    while ( scanner.hasNext() ) {
                        informationString.append(scanner.nextLine());
                    }

                    scanner.close();

                    if ( informationString.charAt(0) != '[' ) {
                        System.out.println("not a correct string");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        MainActivity.this,
                                        "cannot retrieve json :/",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
                    } else {
                        JsonParser parse = new JsonParser();
                        JsonArray dataObject = (JsonArray) parse.parse(String.valueOf(informationString));

                        System.out.println("DATA OBJ: " + dataObject);
                        System.out.println("FIRST ELM: " + dataObject.get(0));

                        plant_data = (JsonObject) dataObject.get(0);

                        // UPDATE THE UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for ( int i = 0; i < params_db.length; i++ ){
                                    ((TextView) paramsValuesViewsList.get(i))
                                            .setText((plant_data.get(params_db[i])).toString().replace("\"", ""));
                                }
                            }
                        });
                    }
                }
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                MainActivity.this,
                                "Could not connect to server:(",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
                this.exception = e;
                e.printStackTrace();
            } finally {
                System.out.println("reached the end :D");
            }

            return null;
        }

        protected void onPostExecute(Result result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            System.out.println(this.exception);
        }

    }

    class PostReq extends AsyncTask<String, Void, Void> {

        private Exception exception;

        @SuppressLint("SetTextI18n")
        protected Void doInBackground(String... urls) {
            try {
                String url = urls[0];
                URL object = new URL(url);

                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");

                JSONObject params = new JSONObject();

                if ( url == URL_GLOBAL + URL_SEND_COMMAND ){
                    params.put("parameter_name", "pump_1");
                    params.put("value", 1);
                }

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
}