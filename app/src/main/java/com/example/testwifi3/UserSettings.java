package com.example.testwifi3;

import android.app.Application;

public class UserSettings extends Application {

    public static final String PREFERENCES = "preferences";

    public static  String CURRENT_IP_ADDRESS = "currentIpAddress";
    public static final String LAST_IP_ADDRESS = "lastIPADDress";

    private String ip_address;

    public String getIPAddress() {
        return ip_address;
    }

    public void setIPAddress(String ipAddress ) {
        this.ip_address = ipAddress;
        this.CURRENT_IP_ADDRESS = ipAddress;
    }
}
