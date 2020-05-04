package com.diary.myday;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CreateDiary extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    private static String FILE_NAME;
    private static String FILE_DIR;
    private String date;
    private String day;

    private ConstraintLayout createLayout;
    private DisplayMetrics displayMetrics;

    private int screenWidth;
    private int screenHeight;

    private int xDelta;
    private int yDelta;
    private int xDelta2;
    private int yDelta2;

    private ImageView image;
    private ImageView frame;
    private int[] stickerIdSave;
    private int[] stickerResSave;
    private int[][] stickerPosSave;
    private static int stickerCount = 0;
    static final int stickerMax = 3;

    static final int SELECT_STICKER_RC = 1;

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

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void create_diary(View v){
        setContentView(R.layout.create_diary);

        getDay();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sad(View v){
        setContentView(R.layout.create_diary);

        getDay();
        Intent arrangeIntent = new Intent(this, ArrangePictureGame.class);
        startActivity(arrangeIntent);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getDay(){
        TextView dateTimeDisplay;
        Calendar calendar;
        SimpleDateFormat dateFormat;
        //String date;
        //String day;
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
        FILE_NAME = "diary_" + dayOfYear + ".txt";
    }

    public void save(View v) throws IOException {
        Context context = getApplicationContext();
        String folder = context.getFilesDir().getAbsolutePath() + FILE_DIR;
        File subFolder = new File(folder);
        if(!subFolder.exists()){
            subFolder.mkdirs();
        }

        String text = "Date: " + day + "\n";
        for(int c = 0; c < 3; c++){
            if(stickerResSave[c] != 0){
                text += "StickerId: " + stickerIdSave[c] + "\n";
                text += "StickerRes: " + stickerResSave[c] + "\n";
                text += "StickerPosX: " + stickerPosSave[c][0]  + "\n";
                text += "StickerPosY: " + stickerPosSave[c][1] + "\n";
            }
        }

        text += "Message: \n";

        EditText dEditText = findViewById(R.id.diary);
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
            case R.id.change_frame:
                createLayout = findViewById(R.id.create_diary);
                frame = new ImageView(CreateDiary.this);
                EditText editText = findViewById(R.id.diary);
                frame.setId(ImageView.generateViewId());
                frame.setImageResource(R.drawable.frame_christmas);
                frame.setAdjustViewBounds(true);
                createLayout.addView(frame);

                ConstraintSet set = new ConstraintSet();
                set.clone(createLayout);
                set.constrainHeight(frame.getId(), ConstraintSet.WRAP_CONTENT);
                set.constrainWidth(frame.getId(), ConstraintSet.WRAP_CONTENT);
                set.connect(frame.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                set.connect(frame.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
                set.connect(frame.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
                set.connect(frame.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
                set.applyTo(createLayout);
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
            if(stickerId != 0) {
                createLayout = findViewById(R.id.create_diary);
                ConstraintSet set = new ConstraintSet();
                set.clone(createLayout);

                image = new ImageView(CreateDiary.this);
                image.setId(ImageView.generateViewId());
                image.setImageResource(stickerId);
                image.setMaxWidth(256);
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
                image.getLocationOnScreen(stickerPosSave[stickerCount]);
                stickerCount++;
            }
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
                    if(image.getId() == stickerIdSave[0]) image.getLocationOnScreen(stickerPosSave[0]);
                    if(image.getId() == stickerIdSave[1]) image.getLocationOnScreen(stickerPosSave[1]);
                    if(image.getId() == stickerIdSave[2]) image.getLocationOnScreen(stickerPosSave[2]);
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
                            System.out.println("boundary right x");
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
                            System.out.println("boundary upper y ");
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
}
