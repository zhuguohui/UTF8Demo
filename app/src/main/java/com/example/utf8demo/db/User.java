package com.example.utf8demo.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author zhuguohui
 * @description:
 * @date :2021/5/14 13:34
 */
@Entity
public class User implements Serializable {


    @PrimaryKey(autoGenerate = true)
    private int uid;


    private String name;

    private String phone;



    /**
     * 发送短信的时候要显示的名字
     */
    private String smsName;




    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSmsName() {
        return smsName;
    }

    public void setSmsName(String smsName) {
        this.smsName = smsName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return uid == user.uid &&
                Objects.equals(name, user.name) &&
                Objects.equals(phone, user.phone) &&
                Objects.equals(smsName, user.smsName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, name, phone, smsName);
    }


}