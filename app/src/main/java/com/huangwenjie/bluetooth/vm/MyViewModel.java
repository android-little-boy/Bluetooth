package com.huangwenjie.bluetooth.vm;

public class MyViewModel extends BaseViewModel {
    private static final String TAG = "MyViewModel";

    public void init() {
        bluetoothHelper.init(mBluetoothAdapter);
    }

//    public void setConnectCallback(ConnectCallback connectCallback) {
//        bluetoothHelper.setConnectCallback(connectCallback);
//    }

    @Override
    protected void onCleared() {
        super.onCleared();
        bluetoothHelper.unInit();
    }

}
