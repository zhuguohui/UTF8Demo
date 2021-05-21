package com.example.zzdx.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * @author zhuguohui
 * @description:
 * @date :2021/5/14 13:36
 */
@Dao
public interface SMSTemplateDao {
    @Query("SELECT * FROM SMSTemplate")
    List<SMSTemplate> getAll();

    @Insert
    void insertAll(SMSTemplate... templates);

    @Delete
    void delete(SMSTemplate template);
} 