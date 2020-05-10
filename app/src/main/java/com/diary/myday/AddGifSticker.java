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

public class AddGifSticker extends AppCompatActivity implements GifStickersAdapter.ItemClickListener{
    GifStickersAdapter adapter;
    int[] gifImages = {R.drawable.octowalk, R.drawable.hamtaramen, R.drawable.laying_cat, R.drawable.bunny_shake_head}; //gif images

    public static final int SELECT_GIF_STICKER_RC = 2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gif_sticker_list);
        checkSettings();

        RecyclerView rvStickers = findViewById(R.id.gif_sticker_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvStickers.setHasFixedSize(true);
        rvStickers.setLayoutManager(layoutManager);
        adapter = new GifStickersAdapter(this, gifImages);
        adapter.setClickListener(this);
        rvStickers.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent();
        System.out.println("sticker id: " + adapter.getItem(position));
        intent.putExtra("GIF_STICKER_ID", adapter.getItem(position));
        setResult(SELECT_GIF_STICKER_RC, intent);

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
