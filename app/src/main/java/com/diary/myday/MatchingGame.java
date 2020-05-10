package com.diary.myday;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MatchingGame extends AppCompatActivity{
    ImageView currentView = null;
    private int countPair = 0;
    final int[] drawable = {R.drawable.porcu_boba,
                            R.drawable.cup_of_coffee,
                            R.drawable.duck,
                            R.drawable.sanriocha_res ,
                            R.drawable.pusheen,
                            R.drawable.catxd};
    Integer[] pos = {0, 1, 2, 3, 4, 5, 0, 1, 2, 3, 4,5};
    int currentPos = -1;

    DisplayMetrics displayMetrics;
    int screenWidth;
    int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matching_game);
        checkSettings();

        displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        List<Integer> posList = Arrays.asList(pos);
        Collections.shuffle(posList);
        posList.toArray(pos);

        MatchingAdapter adapter = new MatchingAdapter(this, screenWidth, screenHeight);
        final GridView gridView = findViewById(R.id.picture_grid);
        gridView.setAdapter(adapter);

        final String unclickable = "unclickable";
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.getTag() == unclickable){
                } else {
                    final ImageView selectedView = (ImageView) view;
                    System.out.println(position);
                    if (currentPos < 0 ) {
                        currentPos = position;
                        currentView = selectedView;
                        selectedView.setImageResource(drawable[pos[position]]);
                    }
                    else {
                        Handler handler = new Handler();
                        if (currentPos == position) {
                            selectedView.setImageResource(R.drawable.card_question);
                        } else if (pos[currentPos] != pos[position]) {
                            selectedView.setImageResource(drawable[pos[position]]);
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    currentView.setImageResource(R.drawable.card_question);
                                    selectedView.setImageResource(R.drawable.card_question);
                                }
                            }, 1000);

                        } else {
                            selectedView.setImageResource(drawable[pos[position]]);
                            currentView.setTag(unclickable);
                            selectedView.setTag(unclickable);
                            currentView.setClickable(false);
                            selectedView.setClickable(false);
                            countPair++;
                            if (countPair == 6) {
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        TextView match_finish = findViewById(R.id.match_finish_text);
                                        match_finish.setVisibility(View.VISIBLE);
                                        gridView.setVisibility(View.INVISIBLE);
                                    }
                                }, 2000);

                            }
                        }
                        currentPos = -1;
                    }
                }
            }
        });
    }

    public void close(View v){
        finish();
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
                        if (text.startsWith("Background: ")) applySettings(text.substring(12));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
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
        }
    }
}
