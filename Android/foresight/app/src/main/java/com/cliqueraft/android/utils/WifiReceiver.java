package com.cliqueraft.android.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cliqueraft.android.adapter.WifiAdapter;
import com.cliqueraft.android.modal.WifiModal;

import java.util.ArrayList;
import java.util.List;

public class WifiReceiver extends BroadcastReceiver {
    WifiManager wifiManager;
    StringBuilder sb;
    RecyclerView wifiDeviceList;
    public WifiReceiver(WifiManager wifiManager, RecyclerView wifiDeviceList) {
        this.wifiManager = wifiManager;
        this.wifiDeviceList = wifiDeviceList;
    }
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            sb = new StringBuilder();
            List<ScanResult> wifiList = wifiManager.getScanResults();
            ArrayList<WifiModal> deviceList = new ArrayList<>();
            for (ScanResult scanResult : wifiList) {
                sb.append("\n").append(scanResult.SSID).append(" - ").append(scanResult.capabilities);
                deviceList.add(new WifiModal(scanResult.SSID));
            }
            Toast.makeText(context, sb, Toast.LENGTH_SHORT).show();
            WifiAdapter wifiAdapter = new WifiAdapter(deviceList, wifiManager);
            wifiDeviceList.setAdapter(wifiAdapter);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
            wifiDeviceList.setLayoutManager(mLayoutManager);
        }
    }
}