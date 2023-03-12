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

import java.util.LinkedList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private MainActivity mainActivity;
    private UserSettings settings;

    Button saveBtn;
    EditText ipAddressInput;

    int counter = 0;
    List<String> items = new LinkedList<>();

    IPsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = ( UserSettings ) getApplication();
        mainActivity = new MainActivity();

        saveBtn = ( Button ) findViewById(R.id.saveBtn);
        ipAddressInput = (EditText) findViewById(R.id.ipAddressInput);


        items.add("code it");

        RecyclerView recyclerView = findViewById(R.id.ipsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this ));
        adapter = new IPsAdapter(items);
        recyclerView.setAdapter(adapter);

        initIPAddressListener();
    }

    private void initIPAddressListener() {

        saveBtn.setOnClickListener( v-> {
            settings.setIPAddress(ipAddressInput.getText().toString());
            SharedPreferences.Editor editor = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE).edit();
            editor.putString(settings.getIPAddress(), settings.getIPAddress());
            editor.apply();

            items.add(ipAddressInput.getText().toString());
            counter += 1;
            adapter.notifyItemInserted(items.size() - 1);

           navigateToMainActivity();

        });
    }

    public void navigateToMainActivity() {
        Log.d("NAVIGATE", "click to navigate to settings :D");

        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}