package com.diary.myday;

import android.annotation.SuppressLint;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    private static final String FILE_NAME = "text.txt";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
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
        date = "Day " + date;
        dateTimeDisplay.setText(date);
        //save edit text
*/


        TextView dateTimeDisplay;
        Calendar calendar;
        SimpleDateFormat dateFormat;
        String date;
        int day;
        String dayCount;
        dateTimeDisplay = (TextView) findViewById(R.id.dayIndicator);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        date = dateFormat.format(calendar.getTime());
        day = calendar.get(Calendar.DAY_OF_YEAR);
        dayCount = "Day "+ day + "\t" + date;
        dateTimeDisplay.setText(dayCount);

    }


    public void save(View v) throws IOException {
        EditText dEditText = findViewById(R.id.diary);
        String text = dEditText.getText().toString();
        FileOutputStream fos = null;

        try{
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());

            dEditText.getText().clear();
            Toast.makeText(getApplicationContext(), "Diary is saved!", Toast.LENGTH_LONG).show();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void load(View v){
        EditText dEditText = findViewById(R.id.diary);
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null){
                sb.append(text).append("\n");
            }
            dEditText.setText(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void menu(View v){
        setContentView(R.layout.menu);
    }

    public void mood(View v){
        setContentView(R.layout.mood_layout);
    }

    public void create(View v){
        setContentView(R.layout.activity_main);
    }

        public void happy(View view){
        Button happyBtn = (Button) findViewById(R.id.happyBtn);
            setContentView(R.layout.activity_main);
        //reward a smiley sticker
    }


        public void okay(View view){
          //  Button okayBtn = (Button) findViewById(R.id.okayBtn);
            setContentView(R.layout.activity_main);
            //go into the diary box page
        }

        public void sad(View view){
        Button sadBtn = (Button) findViewById(R.id.sadBtn);
        //go into minigame
        }

}
