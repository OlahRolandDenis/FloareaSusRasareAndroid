package com.example.testwifi3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    private MainActivity mainActivity;
    private UserSettings settings;

    Button addNewIpBtn, saveBtn;
    EditText ipAddressInput;

    int counter = 0;
    List<String> items = new LinkedList<>();

    IPsAdapter adapter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();


        settings = ( UserSettings ) getApplication();
        mainActivity = new MainActivity();

        saveBtn = ( Button ) findViewById(R.id.saveBtn);
        addNewIpBtn = ( Button ) findViewById(R.id.addNewIpBtn);
        ipAddressInput = (EditText) findViewById(R.id.ipAddressInput);


        Set<String> sharedPreferencesSet = sharedPreferences.getStringSet("ALL_IP_ADDRESSES", null);

        sharedPreferencesSet.forEach(item -> {
            items.add(item);
        });

        System.out.println(items);

        RecyclerView recyclerView = findViewById(R.id.ipsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this ));
        adapter = new IPsAdapter(items);
        recyclerView.setAdapter(adapter);

        initIPAddressListener();
    }

    private void initIPAddressListener() {

        addNewIpBtn.setOnClickListener( v-> {
            settings.setIPAddress(ipAddressInput.getText().toString());

            editor.putString(settings.getIPAddress(), settings.getIPAddress());
            editor.apply();

            items.add(ipAddressInput.getText().toString());
            counter += 1;
            adapter.notifyItemInserted(items.size() - 1);

        });

        saveBtn.setOnClickListener( v -> {
            saveItemsToSharedPreferences();
            navigateToMainActivity();

        });
    }

    private void saveItemsToSharedPreferences() {
        Set<String> set = new HashSet<String>();
        set.addAll(items);

        editor.putStringSet("ALL_IP_ADDRESSES", set).apply();
    }

    public void navigateToMainActivity() {
        Log.d("NAVIGATE", "click to navigate to settings :D");

        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}