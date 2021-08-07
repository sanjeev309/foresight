package com.cliqueraft.android.adapter;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cliqueraft.android.R;
import com.cliqueraft.android.modal.WifiModal;

import java.util.ArrayList;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {

    private ArrayList<WifiModal> wifiList;

    public WifiAdapter(ArrayList<WifiModal> wifiList) {
        this.wifiList = wifiList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wifi_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.wifiName.setText(wifiList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView wifiName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wifiName = itemView.findViewById(R.id.wifi_name);
        }
    }

}
