package com.yuncun.swipeableweekview;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Contains functions to override for WeekViewSwipeable
 */
public class WeekViewAdapter<T> {
    private List<T> mData;

    public WeekViewAdapter( List<T> allDayRecords ){
        mData = allDayRecords;
    }

    /**
     * Set the subtitle text for each day
     * @param tv TextView to override
     * @param index
     */
    protected TextView getTextView(TextView tv, final int index) {
        tv.setVisibility(View.INVISIBLE); //Presumably user will override this if necessary
        return tv;
    }

    /**
     * Set the optional text below the day of week in each circleview. This is usually optional.
     * If you need further styling, use the getCircleView function
     * @param index
     * @return
     */
    protected String getSubText(int index){
        return "";
    }

    /**
     * Return a stroke color for each Day circleview
     * @param index
     * @return Color of strokeview
     */
    protected int getStrokeColor(int index){
        return 0;
    }

    /**
     * Return a fill color for each Day circleview
     * @param index
     * @return
     */
    protected int getFillColor(int index){
        return 0;
    }

    /**
     * Allows full customization of circleview. It will return the entire circleview for the
     * given day. Overrides other circleview functions like getSubtext
     *
     * @param cv - CircleView of given day
     * @param index
     */
    protected CircleView getCircleView(CircleView cv, int index){
        return cv;
    }

    /**
     * Returns the entire week view.
     * @param dv
     * @param index
     * @return
     */
    protected View getDayLayout(View dv, int index){
        return dv;
    }

    public T get(int index){
        return mData.get(index);
    }

    public List<T> getData(){
        return mData;
    }

}
