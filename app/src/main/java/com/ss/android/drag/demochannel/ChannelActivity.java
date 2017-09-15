package com.ss.android.drag.demochannel;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import helper.ItemDragHelperCallback;
import me.yokeyword.itemtouchhelperdemo.R;

public class ChannelActivity extends AppCompatActivity {

    private RecyclerView mRecy;
    int MaxSize;
    Paint paint;
    int mScreenWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mRecy = (RecyclerView) findViewById(R.id.recy);
        WindowManager wm1 = this.getWindowManager();
        mScreenWidth = wm1.getDefaultDisplay().getWidth();
        init();
    }

    private void init() {
        MaxSize=mScreenWidth;
        GridLayoutManager manager = new GridLayoutManager(this, MaxSize);

        paint = new Paint();
        final float densityMultiplier = getApplication().getResources().getDisplayMetrics().density;
        final float scaledPx = 16 * densityMultiplier;
        paint.setTextSize(scaledPx);

        final List<ChannelEntity> items = new ArrayList<>();
        String[] strarray = TestData.getChanelArray();
        for (int i = 0; i < strarray.length; i++) {
            ChannelEntity entity = new ChannelEntity();
            entity.setName(strarray[i]);
            entity.setSpansize((int)(paint.measureText(strarray[i]))+dpToPx(36));
            items.add(entity);
        }

        String []otherchannels =  TestData.getOtherChanelArray();
        final List<ChannelEntity> otherItems = new ArrayList<>();

        for (int i = 0; i < otherchannels.length; i++) {
            ChannelEntity entity = new ChannelEntity();
            entity.setName(otherchannels[i]);
            entity.setSpansize((int)(paint.measureText(otherchannels[i]))+dpToPx(36));
            otherItems.add(entity);
        }

        mRecy.setLayoutManager(manager);
        ItemDragHelperCallback callback = new ItemDragHelperCallback();
        final ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecy);

        mRecy.addItemDecoration(new SpaceItemDecoration(MaxSize));
        ChannelAdapter adapter = new ChannelAdapter(this, helper, items, otherItems,MaxSize);
        mRecy.setHasFixedSize(true);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return MaxSize;
                } else if (position == items.size() + 1) {
                    return MaxSize;
                } else if (position > 0 && position < items.size() + 1) {
                    return items.get(position - 1).getSpansize();
                } else if (position > items.size() + 1 && position - items.size() < otherItems.size()) {
                    return otherItems.get(position - items.size() - 2).getSpansize();
                }
                return 1;
            }
        });
        mRecy.setAdapter(adapter);


        adapter.setOnMyChannelItemClickListener(new ChannelAdapter.OnMyChannelItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(ChannelActivity.this, items.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getNumsA(int num, String temp) {
        StringBuffer sb = new StringBuffer(temp);
        for (int i = 0; i < num; i++) {
            sb.append(temp);
        }
        return sb.toString();

    }
    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getApplication().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getApplication().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

}
