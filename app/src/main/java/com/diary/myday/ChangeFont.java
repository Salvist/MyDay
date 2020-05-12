package com.diary.myday;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChangeFont extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.font_list);
        checkSettings();

        final Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);

        TextView[] fontTV = new TextView[6];
        fontTV[0] = findViewById(R.id.font1);
        fontTV[1] = findViewById(R.id.font2);
        fontTV[2] = findViewById(R.id.font3);
        fontTV[3] = findViewById(R.id.font4);
        fontTV[4] = findViewById(R.id.font5);
        fontTV[5] = findViewById(R.id.font6);

        for(int i = 0; i < 6; i++){
            String fontName = fontTV[i].getText().toString();
            Typeface tf;
            if(fontTV[i].getText().toString().startsWith("Sweety")){
                fontName += ".otf";
            } else {
                fontName += ".ttf";
            }
            tf = Typeface.createFromAsset(getAssets(), fontName);
            fontTV[i].setTypeface(tf);
            fontTV[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f);

            final String finalFontName = fontName;
            fontTV[i].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    intent.putExtra("FONT_FILE_NAME", finalFontName);
                    setResult(5, intent);
                    finish();
                }
            });
        }
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
