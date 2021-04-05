package com.huangwenjie.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.huangwenjie.bluetooth.adapter.MsgAdapter;
import com.huangwenjie.bluetooth.model.Msg;
import com.huangwenjie.bluetooth.vm.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ChatViewModel chatViewModel;
    private static final String TAG = "ChatActivity";
    boolean isRemoteDisconnect = false;
    private List<Msg> mMsgList = new ArrayList<Msg>();
    private RecyclerView recyclerView;
    private EditText editText;
    private ImageButton send;
    private MsgAdapter adapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        String title = intent.getStringExtra("name");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.chat_view_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MsgAdapter(mMsgList);
        recyclerView.setAdapter(adapter);
        editText = findViewById(R.id.message);
        send = findViewById(R.id.btn_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(editText.getText() == null)) {
                    if (TextUtils.isEmpty(editText.getText().toString())) {
                        return;
                    }
                    Msg msg = new Msg(Msg.TYPE_SEND, editText.getText().toString());
                    chatViewModel.write(editText.getText().toString());
                    mMsgList.add(msg);
                    adapter.notifyItemInserted(mMsgList.size() - 1);
                    recyclerView.scrollToPosition(mMsgList.size() - 1);
//                    adapter.notifyDataSetChanged();
//                    Log.d("TAG", "onClick: "+adapter.mMsgList);
                    editText.setText("");
                }
            }
        });
//        recyclerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: 222");
//            }
//        });
        LinearLayout parent = findViewById(R.id.parent);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 333");
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return parent.onTouchEvent(event);
            }

        });
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatViewModel.setConnectCallback();
        chatViewModel.setReceiver();
        chatViewModel.disconnectedDevice.observe(this, new Observer<BluetoothDevice>() {
            @Override
            public void onChanged(BluetoothDevice bluetoothDevice) {
                if (bluetoothDevice != null) {
                    Log.d(TAG, "onChanged: ");
                    isRemoteDisconnect = true;
                    finish();
                }
            }
        });
        chatViewModel.receiveMsg.observe(this, new Observer<Msg>() {
            @Override
            public void onChanged(Msg msg) {
                mMsgList.add(msg);
                adapter.notifyItemInserted(mMsgList.size() - 1);
                recyclerView.scrollToPosition(mMsgList.size() - 1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatViewModel.stopAccept();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isRemoteDisconnect) {
            chatViewModel.write(BluetoothHelper.FLAG_CLOSE);
        }
        chatViewModel.stopConnect();
    }
}