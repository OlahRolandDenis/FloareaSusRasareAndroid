package com.example.testwifi3;

import android.app.Application;

public class UserSettings extends Application {

    public static final String PREFERENCES = "preferences";

    public static String SELECTED_IP_ADDRESS = "selectedIpAddress";

    public String getIPAddress() {
        return this.SELECTED_IP_ADDRESS;
    }

    public void setIPAddress(String ipAddress ) {
        this.SELECTED_IP_ADDRESS = ipAddress;
    }
}
