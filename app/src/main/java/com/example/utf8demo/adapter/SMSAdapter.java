package com.example.utf8demo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utf8demo.NickNameDialog;
import com.example.utf8demo.R;
import com.example.utf8demo.db.SMSItem;
import com.example.utf8demo.db.User;

import java.util.List;

/**
 * @author zhuguohui
 * @description:
 * @date :2021/5/14 14:00
 */
public class SMSAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    List<SMSItem> smsList;



    public SMSAdapter(List<SMSItem> userList) {
        this.smsList = userList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sms_item, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        User user = smsList.get(position).getUser();
        SMSItem sms=smsList.get(position);
        holder.setText(R.id.tv_name, user.getName())
                .setText(R.id.tv_phone, user.getPhone())
                .setText(R.id.tv_nick_name, "昵称:" + user.getSmsName())
                .setText(R.id.tv_content, sms.getSmsContent())
                .setText(R.id.tv_sms_state,sms.getSmsState().getName());


        holder.itemView.setOnClickListener(v -> {
            //修改昵称
            NickNameDialog dialog = new NickNameDialog(holder.itemView.getContext(), user.getSmsName());
            dialog.setOnContentOk(new NickNameDialog.OnContentOk() {
                @Override
                public void onContentOk(String content) {
                    user.setSmsName(content);
                    if (sms.getSmsTemplate() != null) {
                        String newContent = sms.getSmsTemplate().replace("@name", user.getSmsName());
                        sms.setSmsContent(newContent);
                    }
                    notifyItemChanged(position);
                }
            });
            dialog.show();
        });
    }



    @Override
    public int getItemCount() {
        return smsList.size();
    }
}