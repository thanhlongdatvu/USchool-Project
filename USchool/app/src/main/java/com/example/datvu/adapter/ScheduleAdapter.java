package com.example.datvu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.datvu.model.Schedule;
import com.example.datvu.uschool.R;

import java.util.List;

public class ScheduleAdapter extends PagerAdapter {
    List<Schedule> lsSchedule;
    Context context;

    public ScheduleAdapter(List<Schedule> lsSchedule, Context context) {
        this.lsSchedule = lsSchedule;
        this.context = context;
    }

    @Override

    public int getCount() {
        return lsSchedule.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_schedule_item,container,false);

        TextView txtTime = view.findViewById(R.id.txtTime);
        TextView txtAction = view.findViewById(R.id.txtAction);
        TextView txtDescription = view.findViewById(R.id.txtDescription);

        txtTime.setText(lsSchedule.get(position).getTime());
        String action =  convertString(lsSchedule.get(position).getAction());
        txtAction.setText(action);
        String desc = convertString(lsSchedule.get(position).getDescription());
        txtDescription.setText(desc);

        container.addView(view);

        return view;
    }

    private String convertString(String s){
        char[] c = s.toCharArray();
        char[] convert = new char[s.length()*2];
        convert[0] = c[0];
        int j = 1;
        for(int i = 1;i<s.length();i++){
            if(c[i] == '-'){
                convert[j++] = '\n';
                convert[j++] = '\n';
            }
            convert[j++] = c[i];
        }
        return String.valueOf(convert);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
