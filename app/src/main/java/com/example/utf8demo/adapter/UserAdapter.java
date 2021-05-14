package com.example.utf8demo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utf8demo.NickNameDialog;
import com.example.utf8demo.R;
import com.example.utf8demo.db.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zhuguohui
 * @description:
 * @date :2021/5/14 14:00
 */
public class UserAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    List<User> userList;

    Set<User> set = new HashSet<>();

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_item, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        User user = userList.get(position);
        holder.setText(R.id.tv_name, user.getName())
                .setText(R.id.tv_phone, user.getPhone())
                .setText(R.id.tv_nick_name, "昵称:" + user.getSmsName())
                .setText(R.id.tv_content, user.getSmsContent());
        CheckBox checkBox = holder.itemView.findViewById(R.id.checkbox);
        checkBox.setChecked(set.contains(user));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                set.add(user);
            } else {
                set.remove(user);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            //修改昵称
            NickNameDialog dialog = new NickNameDialog(holder.itemView.getContext(),user.getSmsName());
            dialog.setOnContentOk(new NickNameDialog.OnContentOk() {
                @Override
                public void onContentOk(String content) {
                    user.setSmsName(content);
                    if (user.getSmsTemplate() != null) {
                        String sms = user.getSmsTemplate().replace("@name", user.getSmsName());
                        user.setSmsContent(sms);
                    }
                    notifyItemChanged(position);
                }
            });
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}