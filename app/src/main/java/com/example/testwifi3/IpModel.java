package com.example.testwifi3;

public class IpModel {

    String ipAddressValue;
    boolean isChecked;

    public String getIpAddressValue() {
        return ipAddressValue;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public IpModel(String ipAddressValue, boolean isChecked) {
        this.ipAddressValue = ipAddressValue;
        this.isChecked = isChecked;
    }
}
