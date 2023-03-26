package com.example.testwifi3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Ip_RecyclerViewAdapter extends RecyclerView.Adapter<Ip_RecyclerViewAdapter.MyViewHoolder> {

    Context context;
    ArrayList<IpModel> ipModels;

    public Ip_RecyclerViewAdapter(Context context, ArrayList<IpModel> ipModels) {
        this.context = context;
        this.ipModels = ipModels;
    }

    @NonNull
    @Override
    public Ip_RecyclerViewAdapter.MyViewHoolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);

        return new Ip_RecyclerViewAdapter.MyViewHoolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Ip_RecyclerViewAdapter.MyViewHoolder holder, int position) {
        if ( ipModels.get(position).getIsChecked() )
            holder.btnSelectIp.setText("selected");
        else
            holder.btnSelectIp.setText("select");

        holder.tvIpValue.setText(ipModels.get(position).getIpAddressValue());
        holder.btnDeleteIp.setText("x");

    }

    @Override
    public int getItemCount() {
        return ipModels.size();
    }



    public static class MyViewHoolder extends RecyclerView.ViewHolder {
        TextView tvIpValue;
        Button btnSelectIp, btnDeleteIp;

        public MyViewHoolder(@NonNull View itemView) {
            super(itemView);

            tvIpValue = itemView.findViewById(R.id.tvIpValue);
            btnSelectIp = itemView.findViewById(R.id.btnSelectIp2);
            btnDeleteIp = itemView.findViewById(R.id.btnDeleteIp);
        }

    }
}
