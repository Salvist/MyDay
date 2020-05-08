package com.diary.myday;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
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
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private static String FILE_NAME = "diary_";
    private static String FILE_DIR;
    private static String BACKGROUND_COLOR;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        checkSettings();
    }

    public void menu(View v) {
        setContentView(R.layout.activity_main);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat")
    public void create_diary(View v){
        Intent createDiaryIntent = new Intent(this, CreateDiary.class);
        startActivity(createDiaryIntent);
    }

    public void open_diary(View view){
        Intent openDiaryIntent = new Intent(this, LoadDiary.class);
        startActivity(openDiaryIntent);
    }

    public void settings(View view){
        setContentView(R.layout.settings);
        checkSettings();

        TextView bg_color = findViewById(R.id.bg_color);
        bg_color.setText(BACKGROUND_COLOR);
    }


    public void color_toggle(View view){
        String[] color = {"Cyan", "Light Salmon", "Khaki", "Light Grey", "Light Yellow"};

        TextView bg_color = findViewById(R.id.bg_color);
        System.out.println(BACKGROUND_COLOR);
        for(int i = 0; i < color.length; i++){
            if(BACKGROUND_COLOR.contains(color[i])) {
                System.out.println("Color match");
                if(BACKGROUND_COLOR.contains(color[4])){
                    BACKGROUND_COLOR = color[0];
                    applySettings(BACKGROUND_COLOR);
                    bg_color.setText(BACKGROUND_COLOR);
                    return;
                }
                BACKGROUND_COLOR = color[i+1];
                applySettings(BACKGROUND_COLOR);
                bg_color.setText(BACKGROUND_COLOR);
                return;
            }
        }
    }

    public void minigames(View view){
        Intent minigamesIntent = new Intent(this, Minigames.class);
        startActivity(minigamesIntent);
    }

    public void close(View v){
        setContentView(R.layout.activity_main);
    }

    public void checkSettings(){
        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath();
        File subFolder = new File(folder);
        File[] list = subFolder.listFiles();

        FileInputStream fis = null;
        for(File f: list){
            if(f.getName().contains("settings.txt")) {
                System.out.println("settings.txt found");
                try {
                    fis = new FileInputStream(new File(subFolder, f.getName()));
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);

                    String text;
                    while ((text = br.readLine()) != null){
                        if (text.startsWith("Background: ")) {
                            BACKGROUND_COLOR = text.substring(12);
                            applySettings(BACKGROUND_COLOR);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

        }
        System.out.println("settings.txt not found. Creating new one...");
        newSettings("Cyan");
        BACKGROUND_COLOR = "Cyan";
        applySettings(BACKGROUND_COLOR);
    }

    public void newSettings(String color){
        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath();
        File subFolder = new File(folder);

        String newSettings = "Background: " + color;
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(new File(subFolder, "settings.txt"));
            fos.write(newSettings.getBytes());
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

    public void applySettings(String color){
        View current = this.findViewById(android.R.id.content);
        System.out.println("applying color: " + color);
        String[] colorNames = getResources().getStringArray(R.array.colorNames);
        for(int i=0; i<colorNames.length; i++)
        {
            //Getting the color resource id
            TypedArray ta = getResources().obtainTypedArray(R.array.colors);
            int colorToUse = ta.getResourceId(i, 0);

            if(color.contains(colorNames[i])) {
                current.setBackgroundResource(colorToUse);
                System.out.println(colorToUse);
            }

            //System.out.println("color" + "color name: " + colorNames[i]);
            //System.out.println("color" + "color resource id: "+ colorToUse);
        }
        newSettings(color);
    }
}
