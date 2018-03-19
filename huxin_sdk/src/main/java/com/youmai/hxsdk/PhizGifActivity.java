package com.youmai.hxsdk;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.youmai.hxsdk.adapter.PaddingItemDecoration;
import com.youmai.hxsdk.adapter.PhizGifAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/19.
 */
public class PhizGifActivity extends Activity {


    private static final int columns = 4;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(true);
        setContentView(R.layout.hx_activity_phiz_gif);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager manager = new GridLayoutManager(this, columns);
        recyclerView.setLayoutManager(manager);

        List<String> test = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            test.add("" + i);
        }

        PhizGifAdapter adapter = new PhizGifAdapter(this, test);
        recyclerView.setAdapter(adapter);

        int paddingItem = getResources().getDimensionPixelOffset(R.dimen.item_space);
        recyclerView.addItemDecoration(new PaddingItemDecoration(paddingItem));
    }


}
