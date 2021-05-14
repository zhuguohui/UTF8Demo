package com.example.utf8demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
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
    private static final String SMS_ACTION = "ZZZ_ACTION_RAYCLEAR_SEND_SMS";

    private BroadcastReceiver smsBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            System.out.println("ACTION_RAYCLEAR_SEND_SMS");

            if (SMS_ACTION.equals(intent.getAction())) {
                boolean issucess = false;
                String phone = intent.getStringExtra("KEY_CONTACT_PHONE");
                switch (getResultCode()) {
                    case RESULT_OK:

                        issucess = true;

//                        System.out.println("发送成功");
                        Log.i("zzz", phone + "发送成功");
                        break;

                    default:

                        issucess = false;

                        Log.i("zzz", phone + "发送失败");

                        break;

                }
            }
        }
    };
    private IntentFilter mSMSResultFilter = new IntentFilter();
    private StatusViewLayout statusViewLayout;
    private List<User> userList = new ArrayList<>();
    private UserAdapter userAdapter;
    private Button btn_read;

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

        mSMSResultFilter.addAction(SMS_ACTION);
        registerReceiver(smsBroadcastReceiver, mSMSResultFilter);
        statusViewLayout = findViewById(R.id.statusLayout);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //从数据库读取联系人
        loadDataFromDB();


        btn_read = findViewById(R.id.btn_read);
        findViewById(R.id.btn_create_name).setOnClickListener(v -> createName());
        findViewById(R.id.btn_create_sms).setOnClickListener(v -> createSms());

        findViewById(R.id.btn_send).setOnClickListener(v -> {
            for (int i = 0; i < 1; i++) {
                Intent sendintent = new Intent(SMS_ACTION);
                sendintent.putExtra("KEY_CONTACT_PHONE", String.format("15708182104%d", i));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, sendintent, PendingIntent.FLAG_UPDATE_CURRENT);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("15708182104", null, "测试短信" + i, pendingIntent, null);

            }
        });

        findViewById(R.id.btn_read).setOnClickListener(v -> {
            readContacts();

        });
    }


    private void createSms() {
        SMSDialog smsDialog = new SMSDialog(this);
        smsDialog.setOnSmsContentOk(new SMSDialog.OnSmsContentOk() {
            @Override
            public void onContentOk(String content) {
                for (User user : userList) {
                    String sms = content.replace("@name", user.getSmsName());
                    user.setSmsContent(sms);
                }
                userAdapter.notifyDataSetChanged();
            }
        });
        smsDialog.show();

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

    private void createName() {
        for (User user : userList) {
            String name = user.getName();
            boolean haveNickName = false;
            for (String nickName : nickSet) {
                if (name.contains(nickName)) {
                    haveNickName = true;
                    break;
                }
            }
            if (haveNickName) {
                //已经设置了昵称，不处理
                user.setSmsName(name);
                continue;
            }
            if (name.length() == 3) {
                //三个字的名字截取后面两个
                user.setSmsName(name.substring(1));
            } else {
                user.setSmsName(name);
            }

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
        btn_read.setText("正在读取");
        btn_read.setEnabled(false);
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
                btn_read.setText("读取联系人");
                btn_read.setEnabled(true);
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