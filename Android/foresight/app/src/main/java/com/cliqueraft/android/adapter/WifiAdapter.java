package com.cliqueraft.android.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cliqueraft.android.R;
import com.cliqueraft.android.interfaces.ItemClickListener;
import com.cliqueraft.android.modal.WifiModal;

import java.util.ArrayList;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder>{

    private final ArrayList<WifiModal> wifiList;
    private WifiManager wifiManager;

    public WifiAdapter(ArrayList<WifiModal> wifiList, WifiManager wifiManager) {
        this.wifiList = wifiList;
        this.wifiManager = wifiManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wifi_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.wifiName.setText(wifiList.get(position).getSSID());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                openDialog(view, position);
            }
        });
    }

    private void openDialog(View view, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setCancelable(false);
        View dialog = View.inflate(view.getContext(), R.layout.dialog_connect, null);
        EditText editText = dialog.findViewById(R.id.passSharedKey);

        builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = editText.getText().toString();
                connectToWifi(position, password, dialog);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setView(dialog);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void connectToWifi(int position, String password, DialogInterface dialog) {
        String SSID = wifiList.get(position).getSSID();
        if (!TextUtils.isEmpty(SSID) && !TextUtils.isEmpty(password)) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = String.format("\"%s\"", SSID);
            conf.preSharedKey = String.format("\"%s\"", password);

            int netId = wifiManager.addNetwork(conf);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
        }
        dialog.dismiss();
    }


    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView wifiName;
        private ItemClickListener clickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wifiName = itemView.findViewById(R.id.wifi_name);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }

}
