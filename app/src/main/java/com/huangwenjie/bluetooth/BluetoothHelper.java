package com.huangwenjie.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.huangwenjie.bluetooth.callback.ConnectCallback;
import com.huangwenjie.bluetooth.callback.DiscoveryCallback;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;

public class BluetoothHelper {
    private BluetoothAdapter mBluetoothAdapter;
    private DiscoveryCallback discoveryCallback;
    private ConnectCallback connectCallback;
    private static final int FLAG_MSG = 0;  //消息标记
    private static final int FLAG_FILE = 1; //文件标记
    public static final String FLAG_CLOSE = "1111close";//通道关闭消息标记

    public static int DEVICE_VISIBLE_REQUEST_CODE = 1111;

    private String filePath;

    public static UUID MY_UUID = UUID.fromString("5be6b179-fb44-4ca8-b6bd-b87a85e6008e");
    private static final String TAG = "BluetoothHelper";

    private BluetoothHelper() {
    }

    public static class BluetoothHelperProvider {
        private static final BluetoothHelper bluetoothHelper = new BluetoothHelper();

        public static BluetoothHelper get() {
            return bluetoothHelper;
        }
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void init(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
        // 没有开始蓝牙
        if (!this.mBluetoothAdapter.isEnabled()) {
            this.mBluetoothAdapter.enable();
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        MyApplication.getAppContext().registerReceiver(mReceiver, filter);
    }

    public void setConnectCallback(ConnectCallback callback) {
        this.connectCallback = callback;
    }

    /**
     * 获取已配对的蓝牙设备
     *
     * @return 已配对的蓝牙设备
     */
    public Set<BluetoothDevice> getPairedDevice() {
//        List<MyBluetoothDevice> pairedBluetoothDevices = new ArrayList<>();
//        if (pairedDevices.size() > 0) {
//            for (BluetoothDevice device : pairedDevices) {
//                MyBluetoothDevice pairedBluetoothDevice = new MyBluetoothDevice();
//                pairedBluetoothDevice.setName(device.getName());
//                pairedBluetoothDevice.setAddress(device.getAddress());
//                pairedBluetoothDevices.add(pairedBluetoothDevice);
//            }
//        }
        return mBluetoothAdapter.getBondedDevices();
    }

    /**
     * 开始查找可用蓝牙设备
     *
     * @param discoveryCallback 查找结果以callback形式返回
     */
    public boolean startDiscovery(DiscoveryCallback discoveryCallback) {
        this.discoveryCallback = discoveryCallback;
        Log.d(TAG, "startDiscovery: ");
        //当前是否在扫描，如果是就取消当前的扫描，重新扫描
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        //此方法是个异步操作，一般搜索12秒
        return mBluetoothAdapter.startDiscovery();
    }

    /**
     * 结束查找，查找是比较耗资源的，在已经连接上，或者已经找到需要的设备了，建议取消查找
     */
    public void cancelDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
    }

    /**
     * 设置蓝牙的可见性
     *
     * @param times 可见的时间，目前测试Android 10 无效，默认都是120秒
     */
    public static void setDeviceVisible(Activity activity, int times) {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, times);
        activity.startActivityForResult(discoverableIntent, DEVICE_VISIBLE_REQUEST_CODE);

    }

    // 创建一个接受 ACTION_FOUND 的 BroadcastReceiver
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent);
            String action = intent.getAction();
            // 当 Discovery 发现了一个设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 从 Intent 中获取发现的 BluetoothDevice
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 将名字和地址放入要显示的适配器中
                if (discoveryCallback != null) {
                        discoveryCallback.onNewDeviceHasFounded(device);
                }
            }
        }
    };

    public boolean isEnable() {
        return mBluetoothAdapter.isEnabled();
    }

    // 在 onDestroy 中 unRegister
    public void unInit() {
        MyApplication.getAppContext().unregisterReceiver(mReceiver);
    }

    private AcceptThread acceptThread;

    public void startAccept() {
        Log.d(TAG, "startAccept: ");
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread.interrupt();
            acceptThread = null;
        }
        acceptThread = new AcceptThread();
        acceptThread.start();
    }

    public void stopAccept() {
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread.interrupt();
            acceptThread = null;
        }
    }

    private class AcceptThread extends Thread {
        private BluetoothServerSocket mServerSocket;

        public AcceptThread() {
            try {
                mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("bluetooth", MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket socket = null;
            Log.d(TAG, "run: accept 1");
            while (true) {
                try {
                    socket = mServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                if (socket != null) {
                    // 自定义方法
                    manageConnectedSocket(socket);
                    Log.d(TAG, "run: accept 2");
                    try {
                        mServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
        }

        public void cancel() {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private ConnectThread connectThread;

    public void startConnect(BluetoothDevice device) {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread.interrupt();
            connectThread = null;
        }
        connectThread = new ConnectThread(device);
        connectThread.start();
    }

    public void stopConnect() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread.interrupt();
            connectThread = null;
        }
//        if (bluetoothSocket!=null){
//            try {
//                bluetoothSocket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private class ConnectThread extends Thread {
        private BluetoothDevice mDevice;
        private BluetoothSocket mSocket;

        public ConnectThread(BluetoothDevice device) {
            mDevice = device;
            // 这里的 UUID 需要和服务器的一致
            try {
                mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            // 关闭发现设备
            mBluetoothAdapter.cancelDiscovery();
            try {
                Log.d(TAG, "run: 1");
                mSocket.connect();
                Log.d(TAG, "run: 2");
            } catch (IOException connectException) {
                Log.d(TAG, "run: 失败" + connectException.getMessage());
                if (connectCallback != null) {
                    connectCallback.onFailConnect();
                }
                try {
                    mSocket.close();
                } catch (IOException closeException) {
                    return;
                }
                return;
            }
            // 自定义方法
            manageConnectedSocket(mSocket);
        }

        public void cancel() {
            try {
                mSocket.close();
                isRead = false;
                Log.d(TAG, "cancel: ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private BluetoothSocket bluetoothSocket;
    private DataOutputStream dataOutputStream;
    private boolean isRead;

    private void manageConnectedSocket(BluetoothSocket mSocket) {
        this.bluetoothSocket = mSocket;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: manageConnectedSocket ");
                try {
                    dataOutputStream = new DataOutputStream(bluetoothSocket.getOutputStream());
                    DataInputStream in = new DataInputStream(bluetoothSocket.getInputStream());
                    isRead = true;
                    while (isRead) { //循环读取
                        switch (in.readInt()) {
                            case FLAG_MSG: //读取短消息
                                String msg = in.readUTF();
                                if (msg.equals(FLAG_CLOSE)) {
                                    if (connectCallback != null) {
                                        connectCallback.onDisconnected(bluetoothSocket.getRemoteDevice());
                                    }
                                    isRead = false;
                                    Log.d(TAG, "run: reed");
                                }
                                //TODO 回调出去
                                break;
                            case FLAG_FILE: //读取文件
                                File file = new File(filePath);
                                if (!file.exists()) {
                                    file.mkdirs();
                                }
                                String fileName = in.readUTF(); //文件名
                                long fileLen = in.readLong(); //文件长度
                                // 读取文件内容
                                long len = 0;
                                int r;
                                byte[] b = new byte[4 * 1024];
                                FileOutputStream out = new FileOutputStream(filePath + fileName);
                                while ((r = in.read(b)) != -1) {
                                    out.write(b, 0, r);
                                    len += r;
                                    if (len >= fileLen)
                                        break;
                                }
                                break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        if (connectCallback != null) {
            Log.d(TAG, "connectCallback: ");
            connectCallback.onConnected(mSocket.getRemoteDevice());
        }
    }

    private boolean isSending;

    public void write(String msg) {
        if (isSending || TextUtils.isEmpty(msg))
            return;
        isSending = true;
        try {
            dataOutputStream.writeInt(FLAG_MSG);
            dataOutputStream.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        isSending = false;
    }

    public void sendFile(final String filePath) {
        if (isSending || TextUtils.isEmpty(filePath))
            return;
        isSending = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream in = new FileInputStream(filePath);
                    File file = new File(filePath);
                    dataOutputStream.writeInt(FLAG_FILE); //文件标记
                    dataOutputStream.writeUTF(file.getName()); //文件名
                    dataOutputStream.writeLong(file.length()); //文件长度
                    int r;
                    byte[] b = new byte[4 * 1024];
                    while ((r = in.read(b)) != -1) {
                        dataOutputStream.write(b, 0, r);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isSending = false;
            }
        }).start();
    }


}
