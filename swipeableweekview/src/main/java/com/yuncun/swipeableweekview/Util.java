package com.yuncun.swipeableweekview;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * Created by Eric on 10/3/2016.
 */
public class Util {

    /*Interface utility for retrieving a style*/
    public static int getStyledColor(Context context, int attrId)
    {
        TypedArray ta = getTypedArray(context, attrId);
        int color = ta.getColor(0, 0);
        ta.recycle();

        return color;
    }


    private static TypedArray getTypedArray(Context context, int attrId)
    {
        int[] attrs = new int[]{ attrId };
        return context.obtainStyledAttributes(attrs);
    }
}
