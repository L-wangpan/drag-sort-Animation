package com.ss.android.drag.demochannel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import me.yokeyword.itemtouchhelperdemo.R;


/**
 * MyTextView
 * Created by admin on 2017/3/23.
 */
public class MyTextView extends android.support.v7.widget.AppCompatTextView{
    Paint paintBack;
    String text;
    Paint painttext;
    int textSize;
    public MyTextView(Context context) {
        this(context, null,0);

    }

    public MyTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyTextViewStyle, defStyleAttr, 0);
        int n = typedArray.getIndexCount();
        for (int i=0;i<n;i++){
            int t = typedArray.getIndex(i);
            switch (t){
                case R.styleable.MyTextViewStyle_text_Size:
                    textSize = typedArray.getDimensionPixelSize(0,t);
                    break;
            }
        }
        init();
    }
    public void init(){
        painttext = new Paint();
        painttext.setTextSize(textSize);

   /*     final float densityMultiplier = getContext().getResources().getDisplayMetrics().density;
        final float scaledPx = textSize * densityMultiplier;
        painttext.setTextSize(scaledPx);*/
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("",getPaddingLeft(),getHeight()/2,painttext);
    }
}