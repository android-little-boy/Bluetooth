package com.huangwenjie.bluetooth.callback;

import android.bluetooth.BluetoothDevice;

public interface ConnectCallback {
    void onConnected(BluetoothDevice bluetoothDevice);
    void onFailConnect();
    void onDisconnected(BluetoothDevice bluetoothDevice);
}
