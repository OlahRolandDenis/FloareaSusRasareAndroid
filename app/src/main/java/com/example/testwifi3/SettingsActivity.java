package com.example.testwifi3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    private MainActivity mainActivity;
    private UserSettings settings;

    Button saveBtn;
    EditText ipAddressInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = ( UserSettings ) getApplication();
        mainActivity = new MainActivity();

        saveBtn = ( Button ) findViewById(R.id.saveBtn);
        ipAddressInput = (EditText) findViewById(R.id.ipAddressInput);

        initIPAddressListener();
    }

    private void initIPAddressListener() {

        saveBtn.setOnClickListener( v-> {
            settings.setIPAddress(ipAddressInput.getText().toString());
            SharedPreferences.Editor editor = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE).edit();
            editor.putString(settings.getIPAddress(), settings.getIPAddress());
            editor.apply();
            navigateToMainActivity();
        });
    }

    public void navigateToMainActivity() {
        Log.d("NAVIGATE", "click to navigate to settings :D");

        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}