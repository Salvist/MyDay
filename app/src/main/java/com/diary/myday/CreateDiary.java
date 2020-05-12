package com.diary.myday;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class CreateDiary extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    private static String FILE_NAME;
    private static String FILE_DIR;
    private String date;
    private String day;
    private String font = "sweet purple.ttf";

    private ConstraintLayout createLayout;
    private DisplayMetrics displayMetrics;

    private int screenWidth;
    private int screenHeight;

    private int xDelta;
    private int yDelta;
    private int xDelta2;
    private int yDelta2;

    private int happyStickerId;
    private ImageView image;
    private GifImageView gifImageView;
    private int[] stickerIdSave;
    private int[] stickerResSave;
    private Boolean[] stickerAnimate = {false, false, false};
    private int[][] stickerPosSave;
    private static int stickerCount = 0;
    static final int stickerMax = 3;

    int smiley_stickers[] = {R.drawable.smile1, R.drawable.smile2, R.drawable.smile3, R.drawable.smile4};

    static final int SELECT_STICKER_RC = 1;
    static final int SELECT_GIF_STICKER_RC = 2;
    static final int FONT_FILE_NAME_RC = 5;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mood_layout);
        displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        stickerIdSave = new int[stickerMax];
        stickerResSave = new int[stickerMax];
        stickerPosSave = new int[stickerMax][2];

        checkSettings();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void happy_create_diary(View v){
        setContentView(R.layout.create_diary);
        Random rand = new Random();
        happyStickerId = smiley_stickers[rand.nextInt(4)];
        ImageView happy_sticker = findViewById(R.id.smiley_sticker);
        happy_sticker.setImageResource(happyStickerId);
        stickerCount = 0;

        setEditText();
        checkSettings();
        getDay();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void okay_create_diary(View v){
        setContentView(R.layout.create_diary);

        ImageView happy_sticker = findViewById(R.id.smiley_sticker);
        happy_sticker.setVisibility(View.INVISIBLE);
        stickerCount = 0;

        setEditText();
        checkSettings();
        getDay();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sad(View v){
        setContentView(R.layout.create_diary);
        setEditText();
        getDay();

        Random rand = new Random();
        int random_game = rand.nextInt(3);
        if(random_game == 0){
            Intent gameIntent = new Intent(this, TicTacToe.class);
            startActivity(gameIntent);
        }
        if(random_game == 1){
            Intent gameIntent = new Intent(this, ArrangePictureGame.class);
            startActivity(gameIntent);
        }
        if(random_game == 2){
            Intent gameIntent = new Intent(this, MatchingGame.class);
            startActivity(gameIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getDay(){
        TextView dateTimeDisplay;
        Calendar calendar;
        SimpleDateFormat dateFormat;

        int dayOfYear;

        dateTimeDisplay = findViewById(R.id.dayIndicator);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        date = dateFormat.format(calendar.getTime());
        dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        day = "Day " + dayOfYear + " - " + date;
        dateTimeDisplay.setText(day);

        SimpleDateFormat dateFormatSave = new SimpleDateFormat("yyyy");
        String dateSave = dateFormatSave.format(calendar.getTime());
        FILE_DIR = File.separator + dateSave + File.separator;
        FILE_NAME = "diary_" + dayOfYear + ".txt";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void save(View v) throws IOException {
        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath() + FILE_DIR;
        File subFolder = new File(folder);
        if(!subFolder.exists()){
            subFolder.mkdirs();
        }

        EditText dEditText = findViewById(R.id.diary);

        String text = "Date: " + day + "\n";
        text += "font: " + font + "\n";
        text += "HappySticker: " + happyStickerId + "\n";
        for(int c = 0; c < 3; c++){
            if(stickerResSave[c] != 0){
                text += "StickerId: " + stickerIdSave[c] + "\n";
                text += "StickerRes: " + stickerResSave[c] + "\n";
                if(stickerAnimate[c] == true) text += "Animated: true\n";
                else text += "Animated: false\n";
                text += "StickerPosX: " + stickerPosSave[c][0]  + "\n";
                text += "StickerPosY: " + stickerPosSave[c][1] + "\n";
            }
        }

        text += "Message: \n";

        text += dEditText.getText().toString();
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
        finish();
    }

    public void menu(View view){
        finish();
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

    StickersAdapter adapter;
    int[] images = {R.drawable.duck, R.drawable.cup_of_coffee, R.drawable.porcu_boba};
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean onMenuItemClick(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.add_stickers:
                if(stickerCount == stickerMax){
                    Toast.makeText(this, "Only 3 stickers at max!", Toast.LENGTH_SHORT).show();
                    return true;
                }

                Intent addStickerIntent = new Intent(this, AddSticker.class);
                startActivityForResult(addStickerIntent, SELECT_STICKER_RC);
                return true;
            case R.id.change_font:
                Intent changeFontIntent = new Intent(this, ChangeFont.class);
                startActivityForResult(changeFontIntent, FONT_FILE_NAME_RC);
                return true;
            case R.id.add_gif_stickers:
                if(stickerCount == stickerMax){
                    Toast.makeText(this, "Only 3 stickers at max!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent addGifStickerIntent = new Intent(this, AddGifSticker.class);
                startActivityForResult(addGifStickerIntent, SELECT_GIF_STICKER_RC);
                return true;
            default:
                return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_STICKER_RC){
            int stickerId = data.getIntExtra("STICKER_ID", 0);
            System.out.println(stickerId);
            if(stickerId != 0) {
                createLayout = findViewById(R.id.create_diary);
                ConstraintSet set = new ConstraintSet();
                set.clone(createLayout);
                image = new ImageView(CreateDiary.this);
                image.setId(View.generateViewId());
                image.setImageResource(stickerId);
                image.setMaxWidth(300);
                image.setAdjustViewBounds(true);
                image.setOnTouchListener(new touchListener());

                createLayout.addView(image);

                set.constrainHeight(image.getId(), ConstraintSet.WRAP_CONTENT);
                set.constrainWidth(image.getId(), ConstraintSet.WRAP_CONTENT);
                set.connect(image.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                set.connect(image.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                set.connect(image.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                set.connect(image.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
                set.applyTo(createLayout);

                stickerIdSave[stickerCount] = image.getId();
                stickerResSave[stickerCount] = stickerId;

                System.out.println("width " + image.getMaxWidth());

                //image.getLocationOnScreen(stickerPosSave[stickerCount]);
                stickerPosSave[stickerCount][0] = screenWidth / 2 - 128;
                stickerPosSave[stickerCount][1] = screenHeight / 2 - 53;

                stickerCount++;
            }
        }

        if(requestCode == SELECT_GIF_STICKER_RC){
            int stickerId = data.getIntExtra("GIF_STICKER_ID", 0);
            System.out.println("sticker id is: " + stickerId);
            if(stickerId != 0){
                createLayout = findViewById(R.id.create_diary);
                ConstraintSet set = new ConstraintSet();
                set.clone(createLayout);
                gifImageView = new GifImageView(CreateDiary.this);
                try {
                    GifDrawable gifDrawable = new GifDrawable(getResources(), stickerId);
                    gifImageView.setId(View.generateViewId());
                    gifImageView.setTag("animated");
                    gifImageView.setImageDrawable(gifDrawable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                gifImageView.setMaxWidth(300);
                gifImageView.setAdjustViewBounds(true);
                gifImageView.setOnTouchListener(new touchListener());
                createLayout.addView(gifImageView);
                set.constrainHeight(gifImageView.getId(), ConstraintSet.WRAP_CONTENT);
                set.constrainWidth(gifImageView.getId(), ConstraintSet.WRAP_CONTENT);
                set.connect(gifImageView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                set.connect(gifImageView.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                set.connect(gifImageView.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                set.connect(gifImageView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
                set.applyTo(createLayout);

                stickerIdSave[stickerCount] = gifImageView.getId();
                stickerResSave[stickerCount] = stickerId;
                stickerAnimate[stickerCount] = true;

                stickerPosSave[stickerCount][0] = screenWidth / 2 - 128;
                stickerPosSave[stickerCount][1] = screenHeight / 2 - 53;

                stickerCount++;
            }
        }

        if(requestCode == FONT_FILE_NAME_RC){
            String fontFileName = data.getStringExtra("FONT_FILE_NAME");
            if(fontFileName != null){
                EditText dEditText = findViewById(R.id.diary);
                Typeface tf = Typeface.createFromAsset(getAssets(), fontFileName);
                font = fontFileName;

                dEditText.setTypeface(tf);
                dEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
            }
        }

        if(requestCode == RESULT_OK){

        }
    }

    private final class touchListener implements View.OnTouchListener{
        public boolean onTouch(View view, MotionEvent e) {
            //int width = createLayout.getLayoutParams().width;
            //int height = createLayout.getLayoutParams().height;
            int width = view.getWidth();
            int height = view.getHeight() + 210;

            switch(e.getAction()){
                case MotionEvent.ACTION_DOWN:
                    System.out.println("Action was DOWN");
                    xDelta = (int) (view.getX() - e.getRawX());
                    yDelta = (int) (view.getY() - e.getRawY());
                    xDelta2 = (int) (view.getRight());
                    yDelta2 = (int) (view.getBottom());
                    System.out.println(xDelta + " "+ yDelta + " " + xDelta2 + " " + yDelta2);
                    return true;
                case MotionEvent.ACTION_UP:
                    System.out.println("Action was UP");
                    if(view.getTag() == null){
                        System.out.println("view is instance of imageview");
                        if(image.getId() == stickerIdSave[0]) image.getLocationOnScreen(stickerPosSave[0]);
                        if(image.getId() == stickerIdSave[1]) image.getLocationOnScreen(stickerPosSave[1]);
                        if(image.getId() == stickerIdSave[2]) image.getLocationOnScreen(stickerPosSave[2]);
                    } else {
                        System.out.println("view is instance of gifimageview");
                        if(gifImageView.getId() == stickerIdSave[0]) gifImageView.getLocationOnScreen(stickerPosSave[0]);
                        if(gifImageView.getId() == stickerIdSave[1]) gifImageView.getLocationOnScreen(stickerPosSave[1]);
                        if(gifImageView.getId() == stickerIdSave[2]) gifImageView.getLocationOnScreen(stickerPosSave[2]);
                    }

                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (width == screenWidth && height == screenHeight){}
                    else {
                        view.animate()
                                .x(e.getRawX() + xDelta)
                                .y(e.getRawY() + yDelta)
                                .setDuration(0)
                                .start();

                        if (e.getRawX() + xDelta + width > screenWidth) {
                            view.animate()
                                    .x(screenWidth - width)
                                    .setDuration(0)
                                    .start();
                        }
                        if (e.getRawX() + xDelta < 0) {
                            view.animate()
                                    .x(0)
                                    .setDuration(0)
                                    .start();
                        }
                        if (e.getRawY() + yDelta + height > screenHeight) {
                            view.animate()
                                    .y(screenHeight - height)
                                    .setDuration(0)
                                    .start();
                        }
                        if (e.getRawY() + yDelta < 0) {
                            view.animate()
                                    .y(0)
                                    .setDuration(0)
                                    .start();
                        }
                        return true;
                    }
            }
            return true;
        }
    }

    public void setEditText(){
        EditText dEditText = findViewById(R.id.diary);
        Typeface tf = Typeface.createFromAsset(getAssets(), "sweet purple.ttf");

        dEditText.setTypeface(tf);
        dEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
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
