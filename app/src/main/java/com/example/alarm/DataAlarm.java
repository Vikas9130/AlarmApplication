package com.example.alarm;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarm")
public class DataAlarm {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name="day")
    private String day;

    @ColumnInfo(name="hour")
    private int hour;

    @ColumnInfo(name="minute")
    private int minute;

    @ColumnInfo(name="isAm")
    private boolean isAm;

    @ColumnInfo(name="isPm")
    private boolean isPm;

    @Ignore
    public DataAlarm() {
    }



    public DataAlarm(String day, int hour, int minute, boolean isAm) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.isAm = isAm;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean getIsAm() {
        return isAm;
    }

    public void setIsAm(boolean isAm) {
        this.isAm = isAm;
    }

    public boolean getIsPm() {
        return isPm;
    }

    public void setIsPm(boolean isPm) {
        this.isPm = isPm;
    }

}
