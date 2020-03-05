package com.diary.myday;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView dateTimeDisplay;
        Calendar calendar;
        SimpleDateFormat dateFormat;
        String date;

        dateTimeDisplay = (TextView) findViewById(R.id.dayIndicator);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd");
        date = dateFormat.format(calendar.getTime());
        if(date.charAt(0) == '0'){
            date = date.substring(1);
        }
        dateTimeDisplay.setText("Day " + date);
    }
}