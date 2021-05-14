package com.example.utf8demo;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.utf8demo.tools.DensityUtil;

/**
 * @author zhuguohui
 * @description:
 * @date :2021/5/14 15:21
 */
public class SMSDialog extends Dialog {
    public SMSDialog(@NonNull Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_sms_dialog, null, false);
        setContentView(view);
        getWindow().getAttributes().width = DensityUtil.dip2px(context, 300);
        getWindow().getAttributes().height = DensityUtil.dip2px(context, 300);
        getWindow().setAttributes(getWindow().getAttributes());
        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = getWindow().getDecorView().findViewById(R.id.et_content);
                String content = editText.getText().toString().trim();
                dismiss();
                if (onSmsContentOk != null) {
                    onSmsContentOk.onContentOk(content);
                }
            }
        });
    }

    OnSmsContentOk onSmsContentOk;

    public void setOnSmsContentOk(OnSmsContentOk onSmsContentOk) {
        this.onSmsContentOk = onSmsContentOk;
    }

    public interface OnSmsContentOk {
        void onContentOk(String content);
    }


}