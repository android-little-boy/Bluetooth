package com.huangwenjie.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.huangwenjie.bluetooth.vm.MyViewModel;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    List<BluetoothDevice> bluetoothDevices;
    MyViewModel myViewModel;
    Context context;
    boolean isConnecting = false;
    private static final String TAG = "MyAdapter";

    public MyAdapter(List<BluetoothDevice> bluetoothDevices, MyViewModel myViewModel, Context context) {
        this.bluetoothDevices = bluetoothDevices;
        this.myViewModel = myViewModel;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bluetooth_device_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView.setText(bluetoothDevices.get(position).getName()+"\n"+bluetoothDevices.get(position).getAddress());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnecting){
                    ((TextView)v).setText(bluetoothDevices.get(position).getName()+"\n"+"正在连接");
                    myViewModel.startConnect(bluetoothDevices.get(position));
                    isConnecting = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bluetoothDevices.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.name);
        }
    }
}
