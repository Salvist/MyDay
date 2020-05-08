package com.diary.myday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

public class StickersAdapter extends RecyclerView.Adapter<StickersAdapter.ImageViewHolder>{
    private int[] images;
    private LayoutInflater inflater;
    private ItemClickListener mClickListener;

    public StickersAdapter(Context context, int[] images){
        this.inflater = LayoutInflater.from(context);
        this.images = images;
    }

    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker, parent, false);
        ImageViewHolder imageVH = new ImageViewHolder(view);
        return imageVH;
    }

    @Override
    public void onBindViewHolder(StickersAdapter.ImageViewHolder holder, int position){
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

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView sticker;
        public ImageViewHolder(View itemView){
            super(itemView);
            sticker = itemView.findViewById(R.id.sticker);
            itemView.setOnClickListener(this);
        }
        public void onClick(View view){
            if(mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    void setClickListener(ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
