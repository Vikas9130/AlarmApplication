package com.example.alarm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;

import java.util.ArrayList;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private ArrayList<DataAlarm> alarmList;

    public AlarmAdapter(ArrayList<DataAlarm> alarmList) {
        this.alarmList = alarmList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataAlarm alarm = alarmList.get(position);
        String isam="";
        if(alarm.getIsAm()){
            isam = "AM";
        }else{
            isam = "PM";
        }
        String completeTime = alarm.getHour()+" : "+alarm.getMinute()+" : "+isam;

        holder.dayTextView.setText(alarm.getDay());
        holder.timeTextView.setText(completeTime);
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dayTextView;
        public TextView timeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.textViewDay);
            timeTextView = itemView.findViewById(R.id.textViewTime);
        }
    }
}

