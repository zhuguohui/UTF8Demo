package com.example.utf8demo.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utf8demo.R;

/**
 * @author zhuguohui
 * @description:
 * @date :2021/5/14 14:00
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public BaseViewHolder setText(int id, String content) {
        View view = itemView.findViewById(id);
        if (view instanceof TextView) {
            ((TextView) view).setText(content);
        }
        return this;
    }
}