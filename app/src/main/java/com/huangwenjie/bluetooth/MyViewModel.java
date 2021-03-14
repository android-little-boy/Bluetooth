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
    MutableLiveData<BluetoothDevice> disconnectedDevice;

    public MyViewModel() {
        bluetoothDevice = new MutableLiveData<>();
        connectedDevice = new MutableLiveData<>();
        disconnectedDevice = new MutableLiveData<>();
    }

    public void init() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "init:你的设备不支持蓝牙 ");
        } else {
            bluetoothHelper = BluetoothHelper.BluetoothHelperProvider.get();
        }
        bluetoothHelper.init(mBluetoothAdapter);
        bluetoothHelper.setConnectCallback(new ConnectCallback() {
            @Override
            public void onConnected(BluetoothDevice bluetoothDevice) {
                connectedDevice.postValue(bluetoothDevice);
            }

            @Override
            public void onFailConnect() {
                connectedDevice.postValue(null);
            }

            @Override
            public void onDisconnected(BluetoothDevice bluetoothDevice) {
                disconnectedDevice.postValue(bluetoothDevice);
            }
        });

    }
    public boolean isBluetoothEnable(){
        return bluetoothHelper.isEnable();
    }
    public void startAccept(){
        bluetoothHelper.startAccept();
    }
    public void stopConnect(){
        bluetoothHelper.stopConnect();
    }
    public void stopAccept(){
        bluetoothHelper.stopAccept();
    }
    public void write(String msg){
        bluetoothHelper.write(msg);
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
