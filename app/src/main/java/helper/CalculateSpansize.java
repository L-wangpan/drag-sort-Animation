package helper;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.WindowManager;

/**
 * CalculateSpansize
 * Created by admin on 2017/3/22.
 */
public class CalculateSpansize {

    Paint paint;
    public int getSpansize(String text) {

        int size = (int) paint.measureText(text);

        return size;
    }

    public int setSpansize(int width) {
        paint = new Paint();
        int size = (int) (width/(paint.measureText("a")));
        Log.d("Tag",size+"-------SpanCount---------");
        return size;
    }


}