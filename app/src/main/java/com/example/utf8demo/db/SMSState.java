package com.example.utf8demo.db;

/**
 * @author zhuguohui
 * @description:
 * @date :2021/5/21 16:39
 */
public enum SMSState {

    INIT(""), CREATE("创建成功"),SENDDING("发送中"),SEND_SUCCESS("发送成功"),SEND_FAIL("发送失败");


    String name;

    SMSState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
