package com.huangwenjie.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

public class ChatActivity extends AppCompatActivity {
    private MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        myViewModel = new ViewModelProvider(MainActivity.mainActivity).get(MyViewModel.class);
        myViewModel.disconnectedDevice.observe(this, new Observer<BluetoothDevice>() {
            @Override
            public void onChanged(BluetoothDevice bluetoothDevice) {
                if (bluetoothDevice != null) {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myViewModel.write(BluetoothHelper.FLAG_CLOSE);
    }
}