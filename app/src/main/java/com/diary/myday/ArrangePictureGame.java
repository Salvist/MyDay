package com.diary.myday;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ArrangePictureGame extends AppCompatActivity {
    private DisplayMetrics displayMetrics;
    private int screenWidth;
    private int screenHeight;
    ConstraintLayout gameLayout;
    RelativeLayout relLayout;

    TextView finish;
    ArrayList<PuzzlePiece> pieces;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arrange_game);

        displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        checkSettings();
        puzzleGame();

    }

    public void puzzleGame(){
        finish = findViewById(R.id.arrange_finish_text);
        finish.setText("Arrange the puzzle!");

        gameLayout = findViewById(R.id.arrange_game);
        relLayout = findViewById(R.id.layout);
        final ConstraintSet set = new ConstraintSet();
        set.clone(gameLayout);

        final ImageView picture = findViewById(R.id.picture);

        picture.post(new Runnable(){
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run(){
                relLayout = findViewById(R.id.layout);
                pieces = splitImage(picture);
                Collections.shuffle(pieces);
                for(PuzzlePiece piece : pieces){
                    piece.setOnTouchListener(new TouchListener(ArrangePictureGame.this));
                    relLayout.addView(piece);
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) piece.getLayoutParams();
                    lParams.leftMargin = new Random().nextInt(relLayout.getWidth() - piece.width);
                    lParams.topMargin = new Random().nextInt(relLayout.getHeight() - piece.height);
                    piece.setLayoutParams(lParams);
                }
            }
        });
    }

    public ArrayList<PuzzlePiece> splitImage(ImageView picture){
        int pieces = 9;
        int row = 3;
        int col = 3;

        ArrayList<PuzzlePiece> pics = new ArrayList<>(pieces);

        BitmapDrawable drawable = (BitmapDrawable) picture.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        int[] dimensions = getBitmapPositionInside(picture);
        int scaledBitmapLeft = dimensions[0];
        int scaledBitmapTop = dimensions[1];
        int scaledBitmapWidth = dimensions[2];
        int scaledBitmapHeight = dimensions[3];

        int cropWidth = scaledBitmapWidth - 2 * Math.abs(scaledBitmapLeft);
        int cropHeight = scaledBitmapHeight - 2 * Math.abs(scaledBitmapTop);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledBitmapWidth,scaledBitmapHeight,true);
        Bitmap croppedBitmap = Bitmap.createBitmap(scaledBitmap, Math.abs(scaledBitmapLeft), Math.abs(scaledBitmapTop), cropWidth,cropHeight);


        int pieceWidth = cropWidth / col;
        int pieceHeight = cropHeight / row;
        System.out.println(pieceWidth + " " + pieceHeight);

        int yCoord = 0;
        for(int r = 0; r < row; r++){
            int xCoord = 0;
            for(int c = 0; c < col; c++){
                Bitmap pieceBitmap = Bitmap.createBitmap(croppedBitmap, xCoord, yCoord, pieceWidth, pieceHeight);
                PuzzlePiece piece = new PuzzlePiece(getApplicationContext());
                piece.setImageBitmap(pieceBitmap);
                piece.xCoord = xCoord + picture.getLeft();
                piece.yCoord = yCoord + picture.getTop();
                piece.width = pieceWidth;
                piece.height = pieceHeight;
                pics.add(piece);
                xCoord += pieceWidth;
            }
            yCoord += pieceHeight;
        }
        return pics;
    }

    private int [] getBitmapPositionInside(ImageView picture) {
        int[] ret = new int[4];
        if (picture == null || picture.getDrawable() == null) return ret;

        float[] f = new float[9];
        picture.getImageMatrix().getValues(f);

        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        final Drawable d = picture.getDrawable();
        final int originW = d.getIntrinsicWidth();
        final int originH = d.getIntrinsicHeight();

        final int actW = Math.round(originW * scaleX);
        final int actH = Math.round(originH * scaleY);

        ret[2] = actW;
        ret[3] = actH;

        int pictureW = picture.getWidth() - actW;
        int pictureH = picture.getHeight() - actH;
        System.out.println(picture.getWidth() + " " + picture.getHeight());

        int top = (int) pictureH / 2;
        int left = (int) pictureW / 2;

        ret[0] = left;
        ret[1] = top;

        return ret;
    }

    public class TouchListener implements View.OnTouchListener{
        private float xDelta;
        private float yDelta;

        private ArrangePictureGame activity;

        public TouchListener(ArrangePictureGame activity) {
            this.activity = activity;
        }

        ConstraintLayout layout = findViewById(R.id.arrange_game);
        int width = layout.getLayoutParams().width;
        int height = layout.getLayoutParams().height;

        @Override
        public boolean onTouch(View view, MotionEvent e){
            float x = e.getRawX();
            float y = e.getRawY();
            final double tolerance = Math.sqrt(Math.pow(view.getWidth(), 2) + Math.pow(view.getHeight(), 2)) / 5;

            PuzzlePiece piece = (PuzzlePiece) view;
            if (!piece.move) {
                return true;
            }

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
            switch (e.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    xDelta = x - params.leftMargin;
                    yDelta = y - params.topMargin;
                    piece.bringToFront();
                    break;
                case MotionEvent.ACTION_MOVE:
                    params.leftMargin = (int) (x - xDelta);
                    params.topMargin = (int) (y - yDelta);
                    view.setLayoutParams(params);
                    break;
                case MotionEvent.ACTION_UP:
                    int xDiff = Math.abs(piece.xCoord - params.leftMargin);
                    int yDiff = Math.abs(piece.yCoord - params.topMargin);
                    if (xDiff <= tolerance && yDiff <= tolerance) {
                        params.leftMargin = piece.xCoord;
                        params.topMargin = piece.yCoord;
                        piece.setLayoutParams(params);
                        piece.move = false;
                        sendViewToBack(piece);
                        activity.checkGameOver();
                    }
                    break;
            }
            return true;
        }
    }

    public void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup)child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    public void checkGameOver() {
        if (isGameOver()) {
            finish.setText("You finished the puzzle!");
        }
    }

    private boolean isGameOver() {
        for (PuzzlePiece piece : pieces) {
            if (piece.move) {
                return false;
            }
        }

        return true;
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
