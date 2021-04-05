package com.huangwenjie.bluetooth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.huangwenjie.bluetooth.R;
import com.huangwenjie.bluetooth.model.Msg;

import java.util.List;

public class MsgAdapter extends Adapter<MsgAdapter.ViewHolder> {
    private List<Msg> mMsgList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftLinearLayout;
        LinearLayout rightLinearLayout;
        TextView send;
        TextView received;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            leftLinearLayout = itemView.findViewById(R.id.left_linearLayout);
            rightLinearLayout = itemView.findViewById(R.id.right_linearLayout);
            send = itemView.findViewById(R.id.send);
            received = itemView.findViewById(R.id.received);
        }
    }


    public MsgAdapter(List<Msg> mMsgList) {
        this.mMsgList = mMsgList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        if (msg.getType() == Msg.TYPE_RECEIVED) {
            holder.rightLinearLayout.setVisibility(View.GONE);
            holder.leftLinearLayout.setVisibility(View.VISIBLE);
            holder.received.setText(msg.getContent());
        } else if (msg.getType() == Msg.TYPE_SEND) {
            holder.leftLinearLayout.setVisibility(View.GONE);
            holder.rightLinearLayout.setVisibility(View.VISIBLE);
            holder.send.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }


}
