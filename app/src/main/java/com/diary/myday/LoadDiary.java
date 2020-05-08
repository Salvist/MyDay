package com.diary.myday;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class LoadDiary extends AppCompatActivity implements OpenDiaryAdapter.ItemClickListener{
    private static String FILE_NAME = "diary_";
    private static String FILE_DIR;

    ArrayList<String> listDiary = new ArrayList<>();
    OpenDiaryAdapter adapter;

    String[] font = {"sweet purple.ttf", "Fruity Stories.ttf", "Sweety Rasty.otf", "Thysen J italic.ttf", "TYPEWR__.ttf", "Beathy Version.ttf"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.open_diary);
        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath();
        File subFolder = new File(folder);
        File[] list = subFolder.listFiles();

        for(File f: list){
            String name = f.getName();
            listDiary.add(name);
        }
        Collections.sort(listDiary);

        RecyclerView rvPages = findViewById(R.id.diary_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvPages.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvPages.getContext(), layoutManager.getOrientation());
        rvPages.addItemDecoration(dividerItemDecoration);
        adapter = new OpenDiaryAdapter(this, listDiary);
        adapter.setClickListener(this);
        rvPages.setAdapter(adapter);

        checkSettings();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void load(View v, String d){
        setContentView(R.layout.load_diary);
        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath() + FILE_DIR;
        File subFolder = new File(folder);

        TextView dayLoad = findViewById(R.id.load_day);
        TextView dTextView = findViewById(R.id.load_diary);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(subFolder, d));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            String text;
            String day = "";
            String font = null;
            int stickerIdLoad[] = {0, 0, 0};
            Boolean message = false;
            int[][] stickerPosLoad = new int[3][2];

            int c = 0;
            while ((text = br.readLine()) != null){
                if(message == false) {
                    if (text.startsWith("Date: ")) day = text.substring(6);
                    if (text.startsWith("font: ")) font = text.substring(6);
                    if (text.startsWith("StickerId: ") && Integer.parseInt(text.substring(11)) == c+1) continue;
                    if (text.startsWith("StickerId: ")) c++;
                    if (text.startsWith("StickerRes: ")) stickerIdLoad[c] = Integer.parseInt(text.substring(12));
                    if (text.startsWith("StickerPosX: ")) stickerPosLoad[c][0] = Integer.parseInt(text.substring(13));
                    if (text.startsWith("StickerPosY: ")) stickerPosLoad[c][1] = Integer.parseInt(text.substring(13));
                    if (text.startsWith("Message: ")) message = true;
                } else {
                    sb.append(text).append("\n");
                }
            }
            dayLoad.setText(day);

            for(int i = 0; i < 3; ++i){
                if(stickerIdLoad[i] != 0) loadSticker(stickerIdLoad[i], stickerPosLoad[i]);
            }

            dTextView.setText(sb.toString());
            //Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), font[0]);
            //dTextView.setTypeface(typeface);

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadSticker(int stickerIdLoad, int[] stickerPosLoad){
        ConstraintLayout loadLayout = findViewById(R.id.load_page);
        ConstraintSet set = new ConstraintSet();
        int HEIGHT_FIXER = 210;
        stickerPosLoad[1] -= HEIGHT_FIXER;

        set.clone(loadLayout);

        ImageView image = new ImageView(LoadDiary.this);
        image.setId(ImageView.generateViewId());
        image.setImageResource(stickerIdLoad);
        image.setMaxWidth(256);
        image.setAdjustViewBounds(true);
        loadLayout.addView(image);

        set.constrainHeight(image.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainWidth(image.getId(), ConstraintSet.WRAP_CONTENT);
        set.connect(image.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        set.setTranslation(image.getId(), stickerPosLoad[0], stickerPosLoad[1]);

        set.applyTo(loadLayout);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemClick(View view, int position){
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

        if(listDiary.get(position).endsWith(".txt")){
            load(view, listDiary.get(position));
            listDiary.clear();
            return;
        }
        FILE_DIR = File.separator + listDiary.get(position);
        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath() + FILE_DIR;
        File subFolder = new File(folder);
        File[] list = subFolder.listFiles();

        listDiary.clear();
        for(File f: list){
            String name = f.getName();
            listDiary.add(name);
        }
        adapter.notifyDataSetChanged();
    }

    public void edit(View v){

        TextView dTextView = findViewById(R.id.load_diary);
        EditText dEditText = findViewById(R.id.edit_diary);
        dTextView.setVisibility(View.INVISIBLE);
        dEditText.setText(dTextView.getText());
        dEditText.setVisibility(View.VISIBLE);

    }

    public void edit_save(View v){

    }

    public void menu(View v){
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
