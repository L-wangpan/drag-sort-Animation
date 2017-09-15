package com.ss.android.drag.demochannel;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

/**
 * MyStaggeredGridLayoutManager
 * Created by admin on 2017/3/23.
 */
public class MyStaggeredGridLayoutManager extends StaggeredGridLayoutManager {

    public MyStaggeredGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }
}