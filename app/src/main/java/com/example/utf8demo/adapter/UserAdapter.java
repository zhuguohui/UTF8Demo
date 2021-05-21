package com.example.utf8demo.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utf8demo.NickNameDialog;
import com.example.utf8demo.R;
import com.example.utf8demo.db.AppDatabase;
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
                .setText(R.id.tv_nick_name, "昵称:" + user.getSmsName());
        CheckBox checkBox = holder.itemView.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                set.add(user);
            } else {
                set.remove(user);
            }
        });
        checkBox.setChecked(set.contains(user));

        View ivDelete = holder.itemView.findViewById(R.id.iv_delete);
        ivDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.ctx())
                    .setCancelable(true)
                    .setMessage("确定删除联系人")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Thread(() -> {
                                AppDatabase.getInstance(holder.ctx()).userDao().delete(user);
                                checkBox.post(() -> {
                                    userList.remove(user);
                                    notifyItemRemoved(holder.getAdapterPosition());
                                    dialog.dismiss();
                                });
                            }).start();

                        }
                    })
                    .create().show();

        });
        holder.itemView.setOnClickListener(v -> {
            //修改昵称
            NickNameDialog dialog = new NickNameDialog(holder.itemView.getContext(), user.getSmsName());
            dialog.setOnContentOk(new NickNameDialog.OnContentOk() {
                @Override
                public void onContentOk(String content) {
                    new Thread(()->{
                        user.setSmsName(content);
                        AppDatabase.getInstance(holder.ctx()).userDao().update(user);
                        holder.itemView.post(()->{
                            notifyItemChanged(position);
                        });
                    }).start();
                }
            });
            dialog.show();
        });
    }

    public Set<User> getSet() {
        return set;
    }

    public void selectAll() {
        if (set.size() > 0) {
            set.clear();
        } else {
            set.addAll(userList);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}