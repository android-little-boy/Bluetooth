package com.huangwenjie.bluetooth.callback;

import android.bluetooth.BluetoothDevice;


public interface DiscoveryCallback {
    void onNewDeviceHasFounded(BluetoothDevice bluetoothDevice);
}
