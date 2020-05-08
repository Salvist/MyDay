package com.diary.myday;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChangeFont extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.font_list);

        TextView[] fontTV = new TextView[6];
        fontTV[0] = findViewById(R.id.font1);
        fontTV[1] = findViewById(R.id.font2);
        fontTV[2] = findViewById(R.id.font3);
        fontTV[3] = findViewById(R.id.font4);
        fontTV[4] = findViewById(R.id.font5);
        fontTV[5] = findViewById(R.id.font6);

        EditText dEditText = findViewById(R.id.diary);

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
                    Intent intent = new Intent();
                    intent.putExtra("FONT_FILE_NAME", finalFontName);
                    setResult(5, intent);
                    finish();
                }
            });
        }
    }
}
