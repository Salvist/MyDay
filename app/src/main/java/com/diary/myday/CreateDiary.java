package com.diary.myday;

import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class CreateDiary extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, StickersAdapter.ItemClickListener {
    private static String FILE_NAME = "diary_";
    private static String FILE_DIR;
    private ConstraintLayout createLayout;
    private DisplayMetrics displayMetrics;

    private int xDelta;
    private int yDelta;
    private int screenWidth;
    private int screenHeight;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mood_layout);
        displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void create_diary(View v){
        setContentView(R.layout.create_diary);

        getDay();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getDay(){
        TextView dateTimeDisplay;
        Calendar calendar;
        SimpleDateFormat dateFormat;
        String date;
        String day;
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
        FILE_NAME += dayOfYear + ".txt";
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
    @Override
    public boolean onMenuItemClick(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.add_stickers:
                setContentView(R.layout.sticker_list);

                RecyclerView rvStickers = (RecyclerView) findViewById(R.id.sticker_list);
                GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
                rvStickers.setHasFixedSize(true);
                rvStickers.setLayoutManager(layoutManager);
                adapter = new StickersAdapter(this, images);
                adapter.setClickListener(this);
                rvStickers.setAdapter(adapter);
                return true;
            default:
                return false;
        }

    }

    public void menu(View view){
        finish();
    }
    public void onClick(View v){

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onItemClick(View view, int position) {
        //Toast.makeText(this, "You clicked " + adapter.getItemCount() + " on row number " + position, Toast.LENGTH_SHORT).show();
        setContentView(R.layout.create_diary);
        getDay();
        //ConstraintLayout layout = (ConstraintLayout)  findViewById(R.id.create_diary);
        createLayout = findViewById(R.id.create_diary);
        ConstraintSet set = new ConstraintSet();
        set.clone(createLayout);

        ImageView image = new ImageView(CreateDiary.this);
        image.setId(ImageView.generateViewId());
        image.setImageResource(adapter.getItem(position));
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
    }

    private final class touchListener implements View.OnTouchListener{
        public boolean onTouch(View view, MotionEvent e) {
            int width = createLayout.getLayoutParams().width;
            int height = createLayout.getLayoutParams().height;

            switch(e.getAction()){
                case MotionEvent.ACTION_DOWN:
                    xDelta = (int) (view.getX() - e.getRawX());
                    yDelta = (int) (view.getY() - e.getRawY());
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
}
