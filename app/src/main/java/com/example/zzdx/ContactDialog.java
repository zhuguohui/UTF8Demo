package com.example.zzdx;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.zzdx.tools.DensityUtil;

/**
 * @author zhuguohui
 * @description:
 * @date :2021/5/14 15:21
 */
public class ContactDialog extends Dialog {
    public ContactDialog(@NonNull Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_contact_dialog, null, false);
        setContentView(view);
        getWindow().getAttributes().width = DensityUtil.dip2px(context, 300);
        getWindow().getAttributes().height = DensityUtil.dip2px(context, 280);
        getWindow().setAttributes(getWindow().getAttributes());
        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = getWindow().getDecorView().findViewById(R.id.et_name);
                EditText editText1 = getWindow().getDecorView().findViewById(R.id.et_phone);

                dismiss();
                if (onContactCreateListener != null) {
                    onContactCreateListener.onContactCreated(editText.getText().toString().trim(), editText1.getText().toString().trim());
                }
            }
        });
    }

    onCreateContactListener onContactCreateListener;


    public void setOnContactCreateListener(onCreateContactListener onContactCreateListener) {
        this.onContactCreateListener = onContactCreateListener;
    }

    public interface onCreateContactListener {
        void onContactCreated(String name, String phone);
    }


}