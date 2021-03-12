package com.huangwenjie.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huangwenjie.bluetooth.callback.ConnectCallback;
import com.huangwenjie.bluetooth.callback.DiscoveryCallback;

public class MyViewModel extends ViewModel {
    private BluetoothHelper bluetoothHelper;
    private static final String TAG = "MyViewModel";
    MutableLiveData<BluetoothDevice> connectedDevice;
    MutableLiveData<BluetoothDevice> bluetoothDevice;

    public void init() {
        bluetoothDevice = new MutableLiveData<>();
        connectedDevice = new MutableLiveData<>();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "init:你的设备不支持蓝牙 ");
        } else {
            bluetoothHelper = new BluetoothHelper(MyApplication.getAppContext(), mBluetoothAdapter);
        }
        bluetoothHelper.init();
        bluetoothHelper.startAccept();
        bluetoothHelper.setConnectCallback(new ConnectCallback() {
            @Override
            public void onConnected(BluetoothDevice bluetoothDevice) {
                connectedDevice.postValue(bluetoothDevice);
            }

            @Override
            public void onFailConnect() {
                connectedDevice.postValue(null);
            }
        });

    }

    public void startDiscovery() {
        bluetoothHelper.startDiscovery(new DiscoveryCallback() {
            @Override
            public void onNewDeviceHasFounded(BluetoothDevice bluetoothDevice) {
                MyViewModel.this.bluetoothDevice.setValue(bluetoothDevice);
            }
        });
    }

    public void startConnect(BluetoothDevice device) {
        bluetoothHelper.startConnect(device);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        bluetoothHelper.unInit();
    }

}
