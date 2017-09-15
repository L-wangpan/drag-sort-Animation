package com.ss.android.drag.demochannel;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * SpaceItemDecoration
 * Created by admin on 2017/3/21.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private List<ChannelEntity> mMyChannelItems, mOtherChannelItems;
    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

//        if(parent.getChildAdapterPosition(view) %space == 0)
            outRect.left=20;
            outRect.right=2;
            outRect.top=20;
//            Log.d("Tag",)
    }

}