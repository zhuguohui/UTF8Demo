package com.example.zzdx.db;

import androidx.room.Ignore;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author zhuguohui
 * @description:
 * @date :2021/5/21 15:36
 */
public class SMSItem implements Serializable {
    private String id;
    private long createTime;
    private long sendTime;
    private User user;
    private SMSState smsState=SMSState.INIT;

    @Ignore
    private String smsContent;

    @Ignore
    private String smsTemplate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SMSState getSmsState() {
        return smsState;
    }

    public void setSmsState(SMSState smsState) {
        this.smsState = smsState;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }

    public String getSmsTemplate() {
        return smsTemplate;
    }

    public void setSmsTemplate(String smsTemplate) {
        this.smsTemplate = smsTemplate;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SMSItem smsItem = (SMSItem) o;
        return Objects.equals(id, smsItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}