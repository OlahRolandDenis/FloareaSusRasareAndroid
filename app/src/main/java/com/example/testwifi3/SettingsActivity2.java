package com.example.testwifi3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SettingsActivity2 extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ArrayList<IpModel> ipModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);

        RecyclerView recyclerView = findViewById(R.id.mRecyclerView);

        setUpIpModels();

        Ip_RecyclerViewAdapter adapter = new Ip_RecyclerViewAdapter(
                this,
                ipModels
        );

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUpIpModels() {
        Set<String> saved_ips = new HashSet<String>();
        saved_ips = sharedPreferences.getStringSet("ALL_IP_ADDRESSES", null);

        if ( saved_ips != null && saved_ips.size() > 0)
            for (Iterator<String> iterator = saved_ips.iterator(); iterator.hasNext(); ) {
                String ipValue = iterator.next();
                if ( ipValue.equals("1") )
                    ipModels.add(
                            new IpModel(ipValue, true)
                    );
                else
                    ipModels.add(
                            new IpModel(ipValue, false)
                    );
            }
    }

}