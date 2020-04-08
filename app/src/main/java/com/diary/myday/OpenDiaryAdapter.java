package com.diary.myday;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OpenDiaryAdapter extends RecyclerView.Adapter<OpenDiaryAdapter.ViewHolder>{
    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    OpenDiaryAdapter(Context context, List<String> data){
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.diary_pages, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        String page = mData.get(position);
        holder.textView.setText(page);
    }

    @Override
    public int getItemCount(){
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        String y;
        ViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.page_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if(mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    String getItem(int id){
        return mData.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
