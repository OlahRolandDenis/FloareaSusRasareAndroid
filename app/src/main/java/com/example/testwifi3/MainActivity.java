package com.example.testwifi3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    private static final String URL_LOCAL = "https://192.168.4.210";
    private static final String URL_GLOBAL = "https://80.97.250.38:55443";

    private static final String URL_GET_PLANT_DATA = "/oxygenie/get_plant_data.php";
    private static final String URL_SEND_COMMAND = "/oxygenie/send_command.php";

    private LinearLayout paramsLayout;
    ArrayList<TextView> paramsValuesViewsList = new ArrayList<>();
    String[] params_db = { "leds_intensity", "water_level", "temperature", "moist", "sunlight" };

    JsonObject plant_data;

    String selected_pump, input_milis_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NukeSSLCerts.nuke();    // accept all kind of certificates

        setRepeatingAsyncTask("GetParamsTask", 60 * 1000);    // update param values every minute

        new ConnectionTask().execute(); // connect to server and get plant data

        paramsLayout = (LinearLayout) findViewById(R.id.paramsLayout);

        paramsValuesViewsList.add(findViewById(R.id.textLEDValue));
        paramsValuesViewsList.add(findViewById(R.id.textWaterValue));
        paramsValuesViewsList.add(findViewById(R.id.textTemperatureValue));
        paramsValuesViewsList.add(findViewById(R.id.textMoistValue));
        paramsValuesViewsList.add(findViewById(R.id.textSunlightValue));



        ((ImageView) findViewById(R.id.btnRefresh)).setOnClickListener(v -> {
            new GetParamsTask().execute();
        });

        findViewById(R.id.ledCV).setOnClickListener( v-> {
            findViewById(R.id.bgTransparentView).setVisibility(View.VISIBLE);
            findViewById(R.id.bgTransparentView).setAlpha(0.5f);
            findViewById(R.id.ledControlCV).setVisibility(CardView.VISIBLE);
            findViewById(R.id.ledControlCV).setAlpha(1.0f);

            if ( checkExistingCommand() ) {
                Toast.makeText(
                        MainActivity.this,
                        "Another command is being executed. Please wait.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        ((MaterialButton)findViewById(R.id.btnLedON)).setOnClickListener(v -> {
            if ( !checkExistingCommand() ) {
                new PostCommandReqTask().execute("leds_intensity", "100");
            }

            setRepeatingAsyncTask("GetCommandTask", 2000);
        });

        ((MaterialButton)findViewById(R.id.btnLedOFF)).setOnClickListener(v -> {
            if ( !checkExistingCommand() ) {
                new PostCommandReqTask().execute("leds_intensity", "0");
            }

            setRepeatingAsyncTask("GetCommandTask", 2000);
        });

        ((SeekBar) findViewById(R.id.ledSeekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                i /= 5;
                i *= 5;

                ((TextView)findViewById(R.id.ledSeekBarValue)).setText(i + "");
                value = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if ( !checkExistingCommand() ) {
                    new PostCommandReqTask().execute("leds_intensity", Integer.toString(value));
                }

                setRepeatingAsyncTask("GetCommandTask", 2000);
            }
        });

        findViewById(R.id.ledControlCV).findViewById(R.id.btnCloseLedControl).setOnClickListener(v-> {
            findViewById(R.id.ledControlCV).setAlpha(0.0f);
            findViewById(R.id.ledControlCV).setVisibility(CardView.INVISIBLE);
            findViewById(R.id.bgTransparentView).setAlpha(0.0f);
        });

        findViewById(R.id.waterCV).setOnClickListener(v -> {
            if ( checkExistingCommand() ) {
                Toast.makeText(
                        MainActivity.this,
                        "Another command is being executed. Please wait.",
                        Toast.LENGTH_LONG
                ).show();
            }

            findViewById(R.id.pumpsControlCV).setVisibility(CardView.VISIBLE);
            findViewById(R.id.pumpsControlCV).setAlpha(1.0f);
            findViewById(R.id.bgTransparentView).setAlpha(0.5f);
        });

        Spinner spinner = ((Spinner)findViewById(R.id.spinnerPumps));
        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(this, R.array.pumps, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(spinner_adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_item = spinner.getSelectedItem().toString();
                selected_pump = selected_item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        findViewById(R.id.pumpsControlCV).findViewById(R.id.btnClosePumpsControl).setOnClickListener(v -> {
            ((EditText) findViewById(R.id.inputMilisWater)).onEditorAction(EditorInfo.IME_ACTION_DONE);
            ((EditText) findViewById(R.id.inputMilisWater)).setText("");
            findViewById(R.id.pumpsControlCV).setAlpha(0.0f);
            findViewById(R.id.pumpsControlCV).setVisibility(CardView.INVISIBLE);
            findViewById(R.id.bgTransparentView).setAlpha(0.0f);
        });


        ((MaterialButton)((findViewById(R.id.pumpsControlCV).findViewById(R.id.btnAddPumps)))).setOnClickListener(v -> {
            input_milis_value = ((EditText) findViewById(R.id.inputMilisWater)).getText().toString();

            if ( !checkExistingCommand() ) {
                new PostCommandReqTask().execute(selected_pump, input_milis_value);
            }

            setRepeatingAsyncTask("GetCommandTask", 2000);
        });
    }

    private Boolean checkExistingCommand() {
        Boolean command_already_running = null;

        try {
            System.out.println("I am running checkExistingCommand() !!");
            command_already_running = new GetCommandTask().execute().get();

            if ( !command_already_running ) {
                setElements(true);  // // make then enabled
                System.out.println("GOOD TO GO ( findview ) <3<3");
            } else {
                setElements(false); // make then disabled
                System.out.println("BAD TO GO ( findview )://");
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            return command_already_running;
        }
    }

    private void setElements(boolean enabled) {
        LinearLayout leds_layout = ((LinearLayout)((LinearLayout)((CardView)findViewById(R.id.ledControlCV)).getChildAt(0)).getChildAt(1));

        // LEDS LAYOUT
        for (int i = 0; i < leds_layout.getChildCount(); i++) {
            View view = leds_layout.getChildAt(i);
            view.setEnabled(enabled);
        }

        if ( !enabled ){
            ((SeekBar) findViewById(R.id.ledSeekBar)).setThumb(getResources().getDrawable(R.drawable.disabled_thumb));
            ((SeekBar) findViewById(R.id.ledSeekBar)).setProgressDrawable(getResources().getDrawable(R.drawable.disabled_seekbar));
        } else {
            ((SeekBar) findViewById(R.id.ledSeekBar)).setThumb(getResources().getDrawable(R.drawable.seek_thumb));
            ((SeekBar) findViewById(R.id.ledSeekBar)).setProgressDrawable(getResources().getDrawable(R.drawable.seek_bar));
        }

        // PUMPS CONTROL LAYOUT
        findViewById(R.id.btnAddPumps).setEnabled(enabled);
        findViewById(R.id.inputMilisWater).setEnabled(enabled);

    }

    public void navigateToSettings( View view ) {
        Log.d("NAVIGATE", "click to navigate to settings :D");
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    @SuppressLint("ResourceAsColor")
    private void updateParamValues(String[] params_db) {
        for ( int i = 0; i < params_db.length; i++ ){
            String param_to_update = params_db[i].toString();
            String param_value = plant_data.get(param_to_update).toString();
            System.out.println("THIS IS WHAT YOU ARE LOOKING FOR: " + param_to_update + ": " + param_value);

            if ( (param_to_update == "pump_1" || param_to_update == "pump_2" || param_to_update == "pump_3" || param_to_update == "pump_4") ) {
                if ( param_value == "0" || Integer.parseInt(param_value) == 0 ) {
                    ((TextView) paramsValuesViewsList.get(i))
                            .setText("OFF");

                    ((TextView) paramsValuesViewsList.get(i)).setTextColor(Color.parseColor("#DC143C"));
                } else {
                    ((TextView) paramsValuesViewsList.get(i))
                            .setText("ON");

                    ((TextView) paramsValuesViewsList.get(i)).setTextColor(Color.parseColor("#50C878"));
                }
            } else {
                String str_value = (plant_data.get(params_db[i])).toString().replace("\"", "");
                int int_value = Integer.parseInt(str_value);
                String status_color = "#000000";

                switch ( param_to_update ){
                    case "moist":
                        //value = (plant_data.get(params_db[i])).toString().replace("\"", "");

                        if ( int_value == 0 ){
                            str_value = "NEEDS WATER";
                            status_color = "#DC143C";
                        } else {
                            str_value = "OK";
                            status_color = "#50C878";
                        }

                        break;

                    case "water_level":
                        if ( int_value < 10 ){
                            str_value = "EMPTY";
                            status_color = "#DC143C";
                        } else if ( int_value < 30 ){
                            str_value = "ALMOST EMPTY";
                            status_color = "#DC143C";
                        } else if ( int_value < 75 ) {
                            str_value = "OK";
                            status_color = "#94690D";
                        } else {
                            str_value = "FULL";
                            status_color = "#50C878";
                        }

                        break;

                    case "temperature":


                        break;

                    case "sunlight":
                        if ( int_value == 0 ) {
                            str_value = "NOT ENOUGH";
                            status_color = "#DC143C";
                        } else {
                            str_value = "ENOUGH";
                            status_color = "#50C878";
                        }

                        break;

                    case "leds_intensity":
                        if ( int_value == 0 ) {
                            str_value = "OFF";
                            status_color = "#DC143C";   // red
                        } else if ( int_value == 100 ){
                            status_color = "#50C878";   // green
                        } else {
                            status_color = "#94690D"; // yellow
                        }
                        break;

                    default:
                        ((TextView) paramsValuesViewsList.get(i))
                                .setText((plant_data.get(params_db[i])).toString().replace("\"", ""));
                        status_color = "#94690D";
                        break;
                }

                ((TextView) paramsValuesViewsList.get(i))
                        .setText(str_value);

                if ( status_color != "" )
                    ((TextView) paramsValuesViewsList.get(i)).setTextColor(Color.parseColor(status_color));
            }

        }
    }

    private void setRepeatingAsyncTask(String task_class, int interval) {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            if ( task_class == "GetParamsTask" ){
                                GetParamsTask task_to_execute = new GetParamsTask();
                                task_to_execute.execute();
                            } else {
                                if ( !checkExistingCommand() )
                                    timer.cancel();
                            }
                        } catch (Exception e) {
                            // error, do something
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, interval); // interval of one minute
    }

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(
//                                    MainActivity.this,
//                                    "connected to server <3",
//                                    Toast.LENGTH_SHORT
//                            ).show();

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
                    } else {
                        JsonParser parse = new JsonParser();
                        JsonArray dataObject = (JsonArray) parse.parse(String.valueOf(informationString));

                        plant_data = (JsonObject) dataObject.get(0);

                        // UPDATE THE UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateParamValues(params_db);
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
    }

    class GetParamsTask extends AsyncTask<String, Void, Void> {

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

                        plant_data = (JsonObject) dataObject.get(0);

                        // UPDATE THE UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateParamValues(params_db);
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
    }

    class GetCommandTask extends AsyncTask<String, Void, Boolean> {

        Boolean command_being_executed = false;

        protected Boolean doInBackground(String... urls) {
            try {
                URL url = new URL("https://80.97.250.38:55443/oxygenie/get_command.php" );
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                int response_code = conn.getResponseCode();
                if ( response_code != 200 ) {
                    System.out.println("not 200 upsi");
                    throw new RuntimeException("HttpResponseCode: " + response_code);
                } else {

                    StringBuilder informationString  = new StringBuilder();
                    Scanner scanner = new Scanner(url.openStream());

                    while ( scanner.hasNext() ) {
                        informationString.append(scanner.nextLine());
                    }
                    scanner.close();

                    System.out.println("GET COMMAND STRING: " + informationString);

                    if ( informationString.toString().equals("no command") ) {
                        command_being_executed = false; // we are good to send command
                    } else {
                        command_being_executed = true;  // another command is being executed -> we have to wait
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                return command_being_executed;
            }

          //  return this.command_being_executed;
        }
    }


    class PostCommandReqTask extends AsyncTask<String, Void, Void> {
        private Exception exception;

        @SuppressLint("SetTextI18n")
        protected Void doInBackground(String... command) {
            try {
                String url = URL_GLOBAL + URL_SEND_COMMAND;
                URL object = new URL(url);
                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");

                JSONObject params = new JSONObject();

                System.out.println("this is before the if");

                if ( url.equals(URL_GLOBAL + URL_SEND_COMMAND) ){
                    System.out.println("now i am assigning param");

                    params.put("parameter_name", command[0]);
                    params.put("value", Integer.parseInt(command[1]));
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

                    System.out.println("TRYING TO SEE WHICH ONE iS SQL");
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    br.close();
                } else {
                    System.out.println("this is the else before catch");
                    System.out.println(con.getResponseMessage());
                }

            } catch (Exception e) {
                this.exception = e;
                System.out.println("CANNOT SEND COMMAND");
                e.printStackTrace();
            } finally {
                System.out.println("reached the end :D");

//                findViewById(R.id.ledControlCV).setVisibility(View.INVISIBLE);
//
//                findViewById(R.id.bgTransparentView).setAlpha(0.0f);
//                findViewById(R.id.bgTransparentView).setVisibility(View.INVISIBLE);
            }

            return null;
        }

        protected void onPostExecute() {
            // TODO: check this.exception
            // TODO: do something with the feed

            System.out.println(this.exception);
        }
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
}