package com.example.testwifi3;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;

public class UserSettings extends Application {

    public static final String PREFERENCES = "preferences";

    public static String SELECTED_IP_ADDRESS_KEY = "selectedIpAddress";
    public static String CONNECTED_TO_DEVICE = "CONNECTED_TO_DEVICE";

    private boolean is_connected_to_device = false;
    public static String SELECTED_IP_ADDRESS = "selectedIpAddress";
    public static String SELECTED_IP_PORT = "80";

    public String getIPAddress() {
        return SELECTED_IP_ADDRESS;
    }

    public void setIPAddress(String ipAddress) {
        SELECTED_IP_ADDRESS = ipAddress;
    }

    public boolean isIs_connected_to_device() {
        return is_connected_to_device;
    }

    public void setIs_connected_to_device(boolean is_connected_to_device) {
        this.is_connected_to_device = is_connected_to_device;
    }
}