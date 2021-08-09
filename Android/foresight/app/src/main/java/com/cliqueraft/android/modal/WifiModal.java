package com.cliqueraft.android.modal;

public class WifiModal {

    private int id;
    private String SSID;

    public int getId() {
        return id;
    }

    public String getSSID() {
        return SSID;
    }

    public WifiModal(String SSID) {
        this.SSID = SSID;
    }
}
