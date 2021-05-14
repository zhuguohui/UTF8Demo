package com.example.utf8demo.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * @author zhuguohui
 * @description:
 * @date :2021/5/14 13:37
 */
@Database(entities = {User.class, SMSTemplate.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;
    private static final Object sLock = new Object();

    public abstract UserDao userDao();

    public abstract SMSTemplateDao smsTemplateDao();

    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE =
                        Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user.db")
                                .build();
            }
            return INSTANCE;
        }
    }
}