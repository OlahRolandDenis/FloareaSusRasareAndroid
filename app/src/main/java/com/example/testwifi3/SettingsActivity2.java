package com.example.testwifi3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SettingsActivity2 extends AppCompatActivity {


    // screen elements
    EditText tvNewIp;
    Button btnAddNewIp;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private UserSettings settings;
    ArrayList<IpModel> ipModels = new ArrayList<>();
    Set<String> saved_ips_sharedPreferences;
    List<String> saved_ips = new LinkedList<>();

    Ip_RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        settings = ( UserSettings ) getApplication();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);

        saved_ips_sharedPreferences = sharedPreferences.getStringSet("ALL_IP_ADDRESSES", null);

        if ( saved_ips_sharedPreferences != null )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                saved_ips.addAll(saved_ips_sharedPreferences);
            }
            else
                saved_ips.add("hey");

        setUpIpModels();

        tvNewIp = (EditText) findViewById(R.id.tvNewIp);
        btnAddNewIp = (Button) findViewById(R.id.btnAddNewIp);

        RecyclerView recyclerView = findViewById(R.id.mRecyclerView);

        adapter = new Ip_RecyclerViewAdapter(
                this,
                ipModels
        );

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUpIpModels() {
        saved_ips_sharedPreferences = sharedPreferences.getStringSet("ALL_IP_ADDRESSES", null);
        saved_ips.addAll(saved_ips_sharedPreferences);

        if ( saved_ips != null && saved_ips.size() > 0 ){
            for ( String ip_value : saved_ips ) {
                if ( ip_value.equals(sharedPreferences.getString(UserSettings.SELECTED_IP_ADDRESS, settings.SELECTED_IP_ADDRESS)) )
                    ipModels.add(
                            new IpModel(
                                    ip_value,
                                    true
                            )
                    );
                else
                    ipModels.add(
                            new IpModel(
                                    ip_value,
                                    false
                            )
                    );
            }
        }
    }

    public void addNewIP(View view) {
        if ( tvNewIp.getText() != null && settings != null ) {
            ipModels.add(
                    new IpModel(
                            tvNewIp.getText().toString(),
                            false
                    )
            );
            settings.setIPAddress(tvNewIp.getText().toString());

        }

     //   saved_ips.add(tvNewIp.getText().toString());

     //   editor.putStringSet("ALL_IP_ADDRESSES", new HashSet<>(saved_ips));
     //   editor.apply();

     //   navigateToMainActivity();
    }

    private void navigateToMainActivity() {
        Log.d("NAVIGATE", "click to navigate to settings :D");

        Intent intent = new Intent(SettingsActivity2.this, MainActivity.class);
        startActivity(intent);
    }
}