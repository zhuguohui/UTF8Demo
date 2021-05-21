package com.example.utf8demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.utf8demo.adapter.SMSAdapter;
import com.example.utf8demo.db.SMSItem;
import com.example.utf8demo.db.SMSState;
import com.example.utf8demo.db.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class SMSActivity extends AppCompatActivity {
    private static final String SMS_ACTION = "ZGH_SEND_ACTION";
    private static final String SMS_RECEIVE_ACTION = "ZGH_RECEIVE_ACTION";
    public static final String KEY_USER_LIST = "key_user_list";

    private BroadcastReceiver smsBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            System.out.println("ACTION_RAYCLEAR_SEND_SMS");

            if (SMS_ACTION.equals(intent.getAction())) {
                String id = intent.getStringExtra("KEY_CONTACT_PHONE");
                Log.i("zzz", "code=" + getResultCode() + " id=" + id);
                SMSItem smsItem = new SMSItem();
                smsItem.setId(id);
                int index = smsList.indexOf(smsItem);
                if (index >= 0) {
                    SMSItem item = smsList.get(index);
                    switch (getResultCode()) {
                        case RESULT_OK:
                            item.setSmsState(SMSState.SEND_SUCCESS);
                            smsAdapter.notifyItemChanged(index);
                            break;

                        default:
                            item.setSmsState(SMSState.SEND_FAIL);
                            smsAdapter.notifyItemChanged(index);

                            break;

                    }
                }

            }
        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //表示对方成功收到短信
            String id = intent.getStringExtra("KEY_CONTACT_PHONE");
            SMSItem smsItem = new SMSItem();
            smsItem.setId(id);
            int index = smsList.indexOf(smsItem);
            if (index >= 0) {
                SMSItem item = smsList.get(index);
                item.setSmsState(SMSState.RECEIVE);
                smsAdapter.notifyItemChanged(index);
            }
        }
    };
    ArrayList<User> userList = new ArrayList<>();
    ArrayList<SMSItem> smsList = new ArrayList<>();

    private IntentFilter mSMSResultFilter = new IntentFilter();
    SMSAdapter smsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_m_s);
        setTitle("发送短信");
        userList = (ArrayList<User>) getIntent().getSerializableExtra(KEY_USER_LIST);

        for (User user : userList) {
            SMSItem sms = new SMSItem();
            sms.setId(System.currentTimeMillis() + UUID.randomUUID().toString());
            sms.setUser(user);
            smsList.add(sms);
        }


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        smsAdapter = new SMSAdapter(smsList);
        recyclerView.setAdapter(smsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        registerReceiver(smsBroadcastReceiver, new IntentFilter(SMS_ACTION));
        registerReceiver(receiver, new IntentFilter(SMS_RECEIVE_ACTION));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sms, menu);
        return true;
    }

    //复写，菜单响应事件
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.build_sms:
                createSms();
                break;
            case R.id.send_sms:
                doSent();
                break;
            default:
                break;
        }
        return true;
    }

    private void createSms() {
        SMSDialog smsDialog = new SMSDialog(this);
        smsDialog.setOnSmsContentOk(new SMSDialog.OnSmsContentOk() {
            @Override
            public void onContentOk(String content) {
                for (SMSItem sms : smsList) {
                    sms.setSmsTemplate(content);
                    String smsContent = content.replace("@name", sms.getUser().getSmsName());
                    sms.setSmsContent(smsContent);
                }
                smsAdapter.notifyDataSetChanged();
            }
        });
        smsDialog.show();

    }

    private void doSent() {
        SmsManager smsManager = SmsManager.getDefault();
        for (SMSItem smsItem : smsList) {
            smsItem.setSmsState(SMSState.SENDDING);

            Intent sendIntent = new Intent(SMS_ACTION);
            sendIntent.putExtra("KEY_CONTACT_PHONE", smsItem.getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) Calendar.getInstance().getTimeInMillis(), sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent receiveIntent = new Intent(SMS_RECEIVE_ACTION);
            receiveIntent.putExtra("KEY_CONTACT_PHONE", smsItem.getId());
            PendingIntent receivePendingIntent = PendingIntent.getBroadcast(this,  (int) Calendar.getInstance().getTimeInMillis(), receiveIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            smsManager.sendTextMessage(smsItem.getUser().getPhone(), null, smsItem.getSmsContent(), pendingIntent, receivePendingIntent);
        }
        smsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsBroadcastReceiver);
        unregisterReceiver(receiver);
    }
}