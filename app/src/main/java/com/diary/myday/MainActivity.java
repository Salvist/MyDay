package com.diary.myday;

import android.annotation.SuppressLint;
import android.content.Context;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OpenDiaryAdapter.ItemClickListener{

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void menu(View v) {
        setContentView(R.layout.activity_main);
    }

    private static String FILE_NAME = "diary_";
    private static String FILE_DIR;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat")
    public void create_diary(View v){
        setContentView(R.layout.create_diary);

        TextView dateTimeDisplay;
        Calendar calendar;
        SimpleDateFormat dateFormat;
        String date;
        String day;
        int dayOfYear;

        dateTimeDisplay = (TextView) findViewById(R.id.dayIndicator);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        date = dateFormat.format(calendar.getTime());
        dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        day = "Day " + dayOfYear + " - " + date;
        dateTimeDisplay.setText(day);

        SimpleDateFormat dateFormatSave = new SimpleDateFormat("yyyy-MM-dd");
        String dateSave = dateFormatSave.format(calendar.getTime());
        String[] arrOfDate = dateSave.split("-");
        FILE_DIR = File.separator + arrOfDate[0] + File.separator + arrOfDate[1] + File.separator;
        FILE_NAME += arrOfDate[2] + ".txt";

    }


    public void save(View v) throws IOException {
        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath() + FILE_DIR;
        File subFolder = new File(folder);
        if(!subFolder.exists()){
            subFolder.mkdirs();
        }

        EditText dEditText = findViewById(R.id.diary);
        String text = dEditText.getText().toString();
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(new File(subFolder, FILE_NAME));
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
        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath() + FILE_DIR;
        File subFolder = new File(folder);

        EditText dEditText = findViewById(R.id.diary);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(subFolder, FILE_NAME));
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

    OpenDiaryAdapter adapter;
    public void open_diary(View view){
        setContentView(R.layout.open_diary);

        ArrayList<String> diaries = new ArrayList<>();
        diaries.add("diary 1");
        diaries.add("diary 2");

        RecyclerView rvPages = (RecyclerView) findViewById(R.id.diary_list);
        rvPages.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvPages.getContext(), DividerItemDecoration.VERTICAL);
        rvPages.addItemDecoration(dividerItemDecoration);
        adapter = new OpenDiaryAdapter(this, diaries);
        adapter.setClickListener(this);
        rvPages.setAdapter(adapter);
    }

    public void onItemClick(View view, int position){
        Toast.makeText(this, "You clicked " + adapter. getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}
