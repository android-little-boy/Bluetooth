package com.huangwenjie.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;

import com.huangwenjie.bluetooth.vm.ChatViewModel;
import com.huangwenjie.bluetooth.vm.MyViewModel;

public class ChatActivity extends AppCompatActivity {
    private ChatViewModel myViewModel;
    private static final String TAG = "ChatActivity";
    boolean isRemoteDisconnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        myViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        myViewModel.setConnectCallback();
        myViewModel.disconnectedDevice.observe(this, new Observer<BluetoothDevice>() {
            @Override
            public void onChanged(BluetoothDevice bluetoothDevice) {
                if (bluetoothDevice != null) {
                    Log.d(TAG, "onChanged: ");
                    isRemoteDisconnect = true;
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isRemoteDisconnect){
            myViewModel.write(BluetoothHelper.FLAG_CLOSE);
        }
        myViewModel.stopConnect();
    }
}