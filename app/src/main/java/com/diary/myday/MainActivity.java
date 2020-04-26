package com.diary.myday;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
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
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements OpenDiaryAdapter.ItemClickListener{
    private static String FILE_NAME = "diary_";
    private static String FILE_DIR;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
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

    public void load(View v, String d){
        setContentView(R.layout.load_diary);
        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath() + FILE_DIR;
        File subFolder = new File(folder);

        TextView dayLoad = findViewById(R.id.load_day);
        dayLoad.setText(d);
        TextView dTextView = findViewById(R.id.load_diary);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(subFolder, d));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null){
                sb.append(text).append("\n");
            }
            dTextView.setText(sb.toString());
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

    ArrayList<String> listDiary = new ArrayList<>();
    OpenDiaryAdapter adapter;
    public void open_diary(View view){
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

        RecyclerView rvPages = (RecyclerView) findViewById(R.id.diary_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvPages.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvPages.getContext(), layoutManager.getOrientation());
        rvPages.addItemDecoration(dividerItemDecoration);
        adapter = new OpenDiaryAdapter(this, listDiary);
        adapter.setClickListener(this);
        rvPages.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position){
        Toast.makeText(this, "You clicked " + adapter. getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

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
}
