package com.example.utf8demo.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author zhuguohui
 * @description:用来保存短信模板的类
 * @date :2021/5/14 13:40
 */
@Entity
public class SMSTemplate {
    @PrimaryKey
    int tId;

    String title;
    String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return tId;
    }

    public void setId(int tId) {
        this.tId = tId;
    }
}