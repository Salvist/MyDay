package com.diary.myday;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class AddSticker extends AppCompatActivity implements  StickersAdapter.ItemClickListener {
    StickersAdapter adapter;
    int[] images = {R.drawable.duck, R.drawable.cup_of_coffee, R.drawable.porcu_boba,
                    R.drawable.catxd, R.drawable.pug, R.drawable.sanriocha_res,
                    R.drawable.pusheen, R.drawable.monkey_sad, R.drawable.koala,
                    R.drawable.rabbit_peek};

    Intent intent = new Intent();
    public static final int SELECT_STICKER_RC = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sticker_list);
        checkSettings();
        setResult(RESULT_CANCELED, intent);

        RecyclerView rvStickers = findViewById(R.id.sticker_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvStickers.setHasFixedSize(true);
        rvStickers.setLayoutManager(layoutManager);
        adapter = new StickersAdapter(this, images);
        adapter.setClickListener(this);
        rvStickers.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        intent.putExtra("STICKER_ID", adapter.getItem(position));
        setResult(SELECT_STICKER_RC, intent);

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
