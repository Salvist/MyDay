package com.diary.myday;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddSticker extends AppCompatActivity implements  StickersAdapter.ItemClickListener {
    StickersAdapter adapter;
    int[] images = {R.drawable.duck, R.drawable.cup_of_coffee, R.drawable.porcu_boba};
    public static final int SELECT_STICKER_RC = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sticker_list);

        RecyclerView rvStickers = (RecyclerView) findViewById(R.id.sticker_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvStickers.setHasFixedSize(true);
        rvStickers.setLayoutManager(layoutManager);
        adapter = new StickersAdapter(this, images);
        adapter.setClickListener(this);
        rvStickers.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent();
        intent.putExtra("STICKER_ID", adapter.getItem(position));
        setResult(SELECT_STICKER_RC, intent);
        finish();
    }
}
