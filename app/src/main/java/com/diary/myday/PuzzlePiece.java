package com.diary.myday;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;

public class PuzzlePiece extends AppCompatImageView {
    public int xCoord;
    public int yCoord;
    public int width;
    public int height;
    public boolean move = true;

    public PuzzlePiece(Context context){
        super(context);
    }
}
