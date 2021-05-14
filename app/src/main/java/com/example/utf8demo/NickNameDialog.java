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
public class NickNameDialog extends Dialog {
    public NickNameDialog(@NonNull Context context, String oldName) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_nick_name_dialog, null, false);
        setContentView(view);
        getWindow().getAttributes().width = DensityUtil.dip2px(context, 300);
        getWindow().getAttributes().height = DensityUtil.dip2px(context, 200);
        getWindow().setAttributes(getWindow().getAttributes());
        if (oldName != null) {
            EditText editText = getWindow().getDecorView().findViewById(R.id.et_content);
            editText.setText(oldName);
        }
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

    OnContentOk onSmsContentOk;

    public void setOnContentOk(OnContentOk onSmsContentOk) {
        this.onSmsContentOk = onSmsContentOk;
    }

    public interface OnContentOk {
        void onContentOk(String content);
    }


}