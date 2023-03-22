package com.example.testwifi3;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;

public class UserSettings extends Application {

    public static final String PREFERENCES = "preferences";

    public static String SELECTED_IP_ADDRESS = "selectedIpAddress";

    public String getIPAddress() {
        return SELECTED_IP_ADDRESS;
    }

    public void setIPAddress(String ipAddress) {
        SELECTED_IP_ADDRESS = ipAddress;
    }
}