package com.diary.myday;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class MatchingAdapter extends BaseAdapter {
    private final Context context;
    private int imgWidth;
    private int screenHeight;

    public MatchingAdapter(Context context, int w, int h){
        this.context = context;
        imgWidth = w/4;
        screenHeight = h;
    }

    @Override
    public int getCount() {
        return 12;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ImageView imageView;
        if(view == null){
            GridView.LayoutParams gvLP= new GridView.LayoutParams(imgWidth, imgWidth);

            imageView = new ImageView(this.context);
            imageView.setLayoutParams(gvLP);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) view;
        }
        imageView.setImageResource(R.drawable.card_question);

        return imageView;
    }
}
