package com.huangwenjie.bluetooth.model;

public class Msg extends BaseModel{
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SEND=1;
    private int type;
    private String content;

    public Msg(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
