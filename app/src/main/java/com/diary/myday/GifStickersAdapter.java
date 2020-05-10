package com.diary.myday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import pl.droidsonroids.gif.GifImageView;

public class GifStickersAdapter extends RecyclerView.Adapter<GifStickersAdapter.GifImageViewHolder>{
    private int[] images;
    private LayoutInflater inflater;
    private GifStickersAdapter.ItemClickListener mClickListener;

    public GifStickersAdapter(Context context, int[] images){
        this.inflater = LayoutInflater.from(context);
        this.images = images;
    }

    public GifStickersAdapter.GifImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gif_sticker, parent, false);
        GifStickersAdapter.GifImageViewHolder imageVH = new GifStickersAdapter.GifImageViewHolder(view);
        return imageVH;
    }

    @Override
    public void onBindViewHolder(GifStickersAdapter.GifImageViewHolder holder, int position){
        int image_id = images[position];
        holder.sticker.setImageResource(image_id);
    }

    @Override
    public int getItemCount(){
        return images.length;
    }
    public int getItem(int position){
        return images[position];
    }

    public class GifImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        GifImageView sticker;
        public GifImageViewHolder(View itemView){
            super(itemView);
            sticker = itemView.findViewById(R.id.gif_sticker);
            itemView.setOnClickListener(this);
        }
        public void onClick(View view){
            if(mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    void setClickListener(GifStickersAdapter.ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
