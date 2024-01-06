package com.example.alarm;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DataAlarm.class}, version = 1)
public abstract class AlarmDatabase extends RoomDatabase {
    public abstract AlarmDAO alarmDao();
}
