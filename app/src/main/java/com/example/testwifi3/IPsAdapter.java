package com.example.testwifi3;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.shadow.ShadowRenderer;

import java.util.List;

public class IPsAdapter extends RecyclerView.Adapter<IPsVH> {

    List<String> items;
    UserSettings settings;

    public IPsAdapter( List<String> items ) {
        this.items = items;
    }

    @NonNull
    @Override
    public IPsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ip_address_item, parent, false);

        return new IPsVH(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull IPsVH holder, int position) {
        holder.textView.setText(items.get(position));

        if ( items.get(position).equals(UserSettings.SELECTED_IP_ADDRESS) )
            holder.selectBtn.setText("selected");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

class IPsVH extends RecyclerView.ViewHolder {

    TextView textView;
    Button selectBtn;
    private IPsAdapter adapter;

    public IPsVH(@NonNull View itemView) {
        super(itemView);


        textView = itemView.findViewById(R.id.ipItemText);
        selectBtn = (Button) itemView.findViewById(R.id.btnSelectIp);

        itemView.findViewById(R.id.btnSelectIp).setOnClickListener(view -> {
            Log.d("SELECT", "SELECTED " + textView.getText());
            changeIpAddress(textView.getText().toString());
            selectBtn.setText("selected");
        });

        itemView.findViewById(R.id.ipItemDeleteButton).setOnClickListener(view -> {
             adapter.items.remove(getAdapterPosition());
             adapter.notifyItemRemoved(getAdapterPosition());

        });
    }

    private void changeIpAddress(String ipAddress ) {
        UserSettings settings = new UserSettings();

        settings.setIPAddress(ipAddress);
    }

    public IPsVH linkAdapter(IPsAdapter adapter ) {
        this.adapter = adapter;
        return this;
    }
}

