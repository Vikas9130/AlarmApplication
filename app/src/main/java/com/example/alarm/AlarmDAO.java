package com.example.alarm;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AlarmDAO {
    @Insert
    public void addAlarm(DataAlarm dataAlarm);

    @Delete
    public void deleteAlarm(DataAlarm dataAlarm);

    @Query("select * from alarm")
    public List<DataAlarm> getAllAlarm();
}
