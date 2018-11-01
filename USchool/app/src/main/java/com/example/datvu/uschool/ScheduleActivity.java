package com.example.datvu.uschool;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.datvu.adapter.ScheduleAdapter;
import com.example.datvu.model.Schedule;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {
    Toolbar scheduleToolBar;
    ViewPager vpSchedule;
    List<Schedule> lsSchedule;
    ScheduleAdapter adapterSchedule;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        addControls();
        addEvents();
    }

    private void addEvents() {
        db.collection("Schedule").addSnapshotListener(ScheduleActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for(DocumentChange doc : documentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){
                        Schedule schedule = doc.getDocument().toObject(Schedule.class);
                        lsSchedule.add(schedule);
                        adapterSchedule.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void addControls() {
        db = FirebaseFirestore.getInstance();
        scheduleToolBar = findViewById(R.id.scheduleToolbar);
        setSupportActionBar(scheduleToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Thời khóa biểu");

        vpSchedule = findViewById(R.id.vpSchedule);
        lsSchedule = new ArrayList<>();
        adapterSchedule = new ScheduleAdapter(lsSchedule,ScheduleActivity.this);
        vpSchedule.setAdapter(adapterSchedule);
    }
}
