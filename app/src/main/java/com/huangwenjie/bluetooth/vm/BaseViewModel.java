package com.huangwenjie.bluetooth.vm;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huangwenjie.bluetooth.BluetoothHelper;
import com.huangwenjie.bluetooth.callback.ConnectCallback;

public class BaseViewModel extends ViewModel {
    protected BluetoothHelper bluetoothHelper;
    private static final String TAG = "BaseViewModel";
    public MutableLiveData<BluetoothDevice> connectedDevice;
    public MutableLiveData<BluetoothDevice> bluetoothDevice;
    public MutableLiveData<BluetoothDevice> disconnectedDevice;
    protected  BluetoothAdapter mBluetoothAdapter;

    public BaseViewModel() {
        bluetoothDevice = new MutableLiveData<>();
        connectedDevice = new MutableLiveData<>();
        disconnectedDevice = new MutableLiveData<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "init:你的设备不支持蓝牙 ");
        } else {
            bluetoothHelper = BluetoothHelper.BluetoothHelperProvider.get();
        }
    }

    public void setConnectCallback() {
        bluetoothHelper.setConnectCallback(new ConnectCallback() {
            @Override
            public void onConnected(BluetoothDevice bluetoothDevice) {
                Log.d(TAG, "onConnected: ");
                connectedDevice.postValue(bluetoothDevice);
            }

            @Override
            public void onFailConnect() {
                connectedDevice.postValue(null);
            }

            @Override
            public void onDisconnected(BluetoothDevice bluetoothDevice) {
                Log.d(TAG, "onDisconnected: ");
                disconnectedDevice.postValue(bluetoothDevice);
            }
        });
    }

    public boolean isBluetoothEnable() {
        return bluetoothHelper.isEnable();
    }

    public void startAccept() {
        bluetoothHelper.startAccept();
    }

    public void stopConnect() {
        bluetoothHelper.stopConnect();
    }

    public void stopAccept() {
        bluetoothHelper.stopAccept();
    }

    public void write(String msg) {
        bluetoothHelper.write(msg);
    }

    public void startDiscovery() {
        bluetoothHelper.startDiscovery(bluetoothDevice -> BaseViewModel.this.bluetoothDevice.setValue(bluetoothDevice));
    }

    public void startConnect(BluetoothDevice device) {
        bluetoothHelper.startConnect(device);
    }

}
