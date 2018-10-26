package com.example.datvu.uschool;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {
    ListView lsDanhSach;
    ArrayList<String> ds;
    ArrayAdapter<String> adapterDanhSach;

    String DATABASE_NAME = "dbUSchool.db";
    SQLiteDatabase database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        lsDanhSach = findViewById(R.id.lsView);
        ds = new ArrayList<>();
        adapterDanhSach = new ArrayAdapter<>(TestActivity.this,android.R.layout.simple_list_item_1,ds);
        lsDanhSach.setAdapter(adapterDanhSach);

        Cursor cursor = null;
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        cursor = database.query("User",null,
                null,
                null,null,null,null);
        while (cursor.moveToNext())
        {
            String s = cursor.getString(0)+"\n" +
                    cursor.getString(1)+"\n"+
                    cursor.getString(2)+"\n"+
                    cursor.getString(3)+"\n"+
                    cursor.getString(4)+"\n"+
                    cursor.getString(5)+"\n"+
                    cursor.getString(6);
            ds.add(s);
        }
        adapterDanhSach.notifyDataSetChanged();
    }
}
