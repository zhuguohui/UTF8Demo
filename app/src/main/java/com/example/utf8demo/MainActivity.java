package com.example.utf8demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.utf8demo.adapter.UserAdapter;
import com.example.utf8demo.db.AppDatabase;
import com.example.utf8demo.db.User;
import com.example.utf8demo.db.UserDao;
import com.example.view.StatusViewLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private StatusViewLayout statusViewLayout;
    private List<User> userList = new ArrayList<>();
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkPermission(Manifest.permission.SEND_SMS) || !checkPermission(Manifest.permission.READ_CONTACTS)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS}, 1);
        }

        setTitle("选择联系人");
        statusViewLayout = findViewById(R.id.statusLayout);
        recyclerView = findViewById(R.id.recyclerView);
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //从数据库读取联系人
        loadDataFromDB();
    }


    private Set<String> nickSet = new HashSet<>();

    {
        nickSet.add("舅");
        nickSet.add("老师");
        nickSet.add("师傅");
        nickSet.add("妈");
        nickSet.add("姑");
        nickSet.add("姨");
    }

    private String getNickName(String name) {
        boolean haveNickName = false;
        for (String nickName : nickSet) {
            if (name.contains(nickName)) {
                haveNickName = true;
                break;
            }
        }
        if (haveNickName) {
            //已经设置了昵称，不处理
            return name;
        }
        if (name.length() == 3) {
            //三个字的名字截取后面两个
            return name.substring(1);
        } else {
            return name;
        }
    }

    private void createName() {
        for (User user : userList) {
            String name = user.getName();
            user.setSmsName(getNickName(name));

        }
        new Thread(() -> {
            for (User user : userList) {
                AppDatabase.getInstance(this).userDao().update(user);
            }
        }).start();
        userAdapter.notifyDataSetChanged();
    }

    private void readContacts() {
        statusViewLayout.showLoading();

        new Thread(() -> {
            UserDao dao = AppDatabase.getInstance(this).userDao();
            List<User> insertUserList = new ArrayList<>();
            ArrayList<MyContacts> contacts = ContactUtils.getAllContacts(this);
            int addCount = 0;
            int updateCount = 0;
            for (MyContacts contacts1 : contacts) {
                User dbUser = null;
                boolean isSame = false;
                for (User user : userList) {
                    if (user != null && user.getPhone() != null && user.getPhone().equals(contacts1.phone)) {
                        //找到了
                        dbUser = user;
                        if (user.getName() != null && user.getName().equals(contacts1.name)) {
                            isSame = true;
                        }
                        break;
                    }
                }
                if (isSame) {
                    continue;
                }
                User user = new User();
                if (dbUser != null) {
                    updateCount++;
                    user.setSmsName(dbUser.getSmsName());
                    dao.delete(dbUser);
                } else {
                    addCount++;
                }
                user.setName(contacts1.name);
                user.setPhone(contacts1.phone);
                insertUserList.add(user);

            }
            dao.insertAll(insertUserList.toArray(new User[0]));

            String msg = "更新" + updateCount + "个联系人 新增" + addCount + "个联系人";
            getWindow().getDecorView().post(() -> {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                loadDataFromDB();
            });
        }).start();

    }


    private void loadDataFromDB() {
        statusViewLayout.showLoading();
        new Thread(() -> {
            UserDao dao = AppDatabase.getInstance(this).userDao();
            userList.clear();
            userList.addAll(dao.getAll());
            getWindow().getDecorView().post(() -> {
                if (userList.isEmpty()) {
                    statusViewLayout.showEmpty();
                } else {
                    userAdapter.notifyDataSetChanged();
                    statusViewLayout.showContent();
                }
            });
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //复写，菜单响应事件
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.read_contacts:
                readContacts();
                break;

            case R.id.build_nick_name:
                createName();
                break;
            case R.id.select_all:
                userAdapter.selectAll();
                break;
            case R.id.create_sms:
                Intent intent = new Intent(this, SMSActivity.class);
                ArrayList<User> users = new ArrayList<>(userAdapter.getSet());
                intent.putExtra(SMSActivity.KEY_USER_LIST, users);
                startActivity(intent);
                break;
            case R.id.build_add_contact:
                addContact();

                break;
            case R.id.clear_contacts:
                new Thread(()->{
                    UserDao userDao= AppDatabase.getInstance(this).userDao();
                    for(User user:userList){
                        userDao.delete(user);
                    }
                    userList.clear();
                    runOnUiThread(()->{
                        userAdapter.notifyDataSetChanged();
                    });
                }).start();
                break;
            default:
                break;
        }
        return true;
    }

    private void addContact() {
        ContactDialog contactDialog = new ContactDialog(this);
        contactDialog.setOnContactCreateListener(new ContactDialog.onCreateContactListener() {
            @Override
            public void onContactCreated(String name, String phone) {
                User user = new User();
                user.setName(name);
                user.setPhone(phone);
                user.setSmsName(getNickName(name));
                new Thread(() -> {
                    AppDatabase.getInstance(MainActivity.this).userDao().insertAll(user);
                    runOnUiThread(() -> {
                        userList.add(0, user);
                        userAdapter.notifyItemInserted(0);
                        recyclerView.scrollToPosition(0);
                    });
                }).start();
            }
        });
        contactDialog.show();
    }


    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里写操作 如send（）； send函数中New SendMsg （号码，内容）；
                } else {
                    Toast.makeText(this, "你没启动权限", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
        }
    }
}