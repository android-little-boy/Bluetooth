package com.huangwenjie.bluetooth.vm;

import androidx.lifecycle.MutableLiveData;

import com.huangwenjie.bluetooth.callback.ReceiverCallback;
import com.huangwenjie.bluetooth.model.Msg;

public class ChatViewModel extends BaseViewModel {
    public MutableLiveData<Msg> receiveMsg;

    public ChatViewModel() {
        receiveMsg = new MutableLiveData<>();
    }

    public void setReceiver() {
        bluetoothHelper.setReceiverCallback(new ReceiverCallback() {
            @Override
            public void onReceive(String msg) {
                Msg receiveMsg = new Msg(Msg.TYPE_RECEIVED,msg);
                ChatViewModel.this.receiveMsg.postValue(receiveMsg);
            }
        });
    }
}
