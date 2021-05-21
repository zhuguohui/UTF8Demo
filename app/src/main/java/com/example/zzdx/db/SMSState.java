package com.example.zzdx.db;

import android.graphics.Color;

/**
 * @author zhuguohui
 * @description:
 * @date :2021/5/21 16:39
 */
public enum SMSState {

    INIT(""), CREATE("创建成功"),SENDDING("发送中","#4285F4"),SEND_SUCCESS("发送成功","#34A853"),SEND_FAIL("发送失败","#EA4335"),RECEIVE("已接收","#34A853");


    String name;
    int color;
    SMSState(String name, String color) {
        this.name = name;
        this.color = Color.parseColor(color);
    }
    SMSState(String name, int color) {
        this.name = name;
        this.color = color;
    }

    SMSState(String name) {
        this(name, Color.parseColor("#000000"));
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}
