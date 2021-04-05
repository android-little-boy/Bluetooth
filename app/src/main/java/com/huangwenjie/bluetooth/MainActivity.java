package com.huangwenjie.bluetooth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.huangwenjie.bluetooth.adapter.MyAdapter;
import com.huangwenjie.bluetooth.vm.MyViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    private static final String TAG = "MainActivity";
    private MyAdapter myAdapter;
    private MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.init();
        BluetoothHelper.setDeviceVisible(this,120);
        myViewModel.bluetoothDevice.observe(this, new Observer<BluetoothDevice>() {
            @Override
            public void onChanged(BluetoothDevice bluetoothDevice) {
                bluetoothDevices.add(bluetoothDevice);
                myAdapter.notifyDataSetChanged();
            }
        });
        myViewModel.connectedDevice.observe(this, new Observer<BluetoothDevice>() {
            @Override
            public void onChanged(BluetoothDevice bluetoothDevice) {
                Log.d(TAG, "onChanged: ");
                if (bluetoothDevice!=null){
                    Intent intent = new Intent(MainActivity.this,ChatActivity.class);
                    intent.putExtra("name",bluetoothDevice.getName());
                    startActivity(intent);
                }else {
                    myAdapter.isConnecting = false;
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        myAdapter = new MyAdapter(bluetoothDevices, myViewModel, this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
        registerReceiver(mReceiver, makeFilter());
        Button button = findViewById(R.id.find);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothDevices.clear();
                myAdapter.notifyDataSetChanged();
                myViewModel.startDiscovery();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothHelper.DEVICE_VISIBLE_REQUEST_CODE) {
            Log.d("TAG", "onActivityResult: " + resultCode + " " + data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        while (!myViewModel.isBluetoothEnable()){
        }
        Log.d(TAG, "onResume: ");
        myAdapter.isConnecting = false;
        myViewModel.setConnectCallback();
        myViewModel.startAccept();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return filter;
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.e("TAG", "TURNING_ON");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.e("TAG", "STATE_ON");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.e("TAG", "STATE_TURNING_OFF");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Log.e("TAG", "STATE_OFF");
                            break;
                    }
                    break;
            }
        }
    };
}