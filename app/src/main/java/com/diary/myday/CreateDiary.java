package com.diary.myday;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

public class CreateDiary extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static String FILE_NAME = "diary_";
    private static String FILE_DIR;


    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mood_layout);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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

        SimpleDateFormat dateFormatSave = new SimpleDateFormat("yyyy");
        String dateSave = dateFormatSave.format(calendar.getTime());
        FILE_DIR = File.separator + dateSave + File.separator;
        FILE_NAME += dayOfYear + ".txt";
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


    public void option(View view) {
        Button btn_popup = (Button) findViewById(R.id.option);
        btn_popup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                PopupMenu popup = new PopupMenu(CreateDiary.this, v);
                popup.setOnMenuItemClickListener(CreateDiary.this);
                popup.inflate(R.menu.popup_option);
                popup.show();
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.add_stickers:
                return true;

            default:
                return false;
        }

    }

    public void menu(View view){
        ConstraintLayout layout = (ConstraintLayout)  findViewById(R.id.create_diary);
        ImageView image = new ImageView(CreateDiary.this);
        image.setImageResource(R.drawable.porcu_boba);
        image.setAdjustViewBounds(true);
        image.setMaxWidth(150);
        image.setMaxHeight(150);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layout.addView(image, params);
        //finish();
    }
    public void onClick(View v){

    }
}
