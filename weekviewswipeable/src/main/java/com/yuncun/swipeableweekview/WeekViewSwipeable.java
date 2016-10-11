package com.yuncun.swipeableweekview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Eric on 3/12/2016.
 */

public class WeekViewSwipeable extends LinearLayout {
    private static final String TAG = "WeekViewSwipeable";

    //Defaults

    public static final int DEFAULT_STROKE_COLOR = Color.GRAY;
    public static final int DEFAULT_FILL_COLOR = Color.GRAY;
    public static final int DEFAULT_CIRCLE_TEXT_COLOR = Color.GRAY;
    //Note: The margin attrs currently are not customizable, because they are involved in some calculations
    //for determining the size of the views. This may change in the future.
    public static final int DEFAULT_CIRCLE_LEFTMARGIN = 4;
    public static final int DEFAULT_CIRCLE_RIGHTMARGIN = 4;
    public static final int DEFAULT_CIRCLE_TOPMARGIN = 2;
    public static final int DEFAULT_CRICLE_BOTMARGIN = 0;
    public static final int DEFAULT_NAVBUTTON_WIDTH = 36;

    //Attrs
    private int _circleTextColor = DEFAULT_CIRCLE_TEXT_COLOR;
    private int _strokecolor = DEFAULT_STROKE_COLOR;
    private int _fillcolor = DEFAULT_FILL_COLOR;
    private int _navwidth = DEFAULT_NAVBUTTON_WIDTH;

    public AgcViewPager viewPager;
    private ViewPagerAdapter vpadapter;
    public WeekViewAdapter wvadapter;
    public ImageButton leftNav;
    public ImageButton rightNav;
    protected boolean navEnabled = true;

    public WeekViewSwipeable(Context context) {
        this(context, null);
    }

    public WeekViewSwipeable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.WeekCalendarView, 0, 0);

        _fillcolor = a.getColor(R.styleable.WeekCalendarView_circleDefaultFillColor, _fillcolor);
        _strokecolor = a.getColor(R.styleable.WeekCalendarView_circleDefaultStrokeColor, _strokecolor);
        _circleTextColor = getResources().getColor(android.R.color.primary_text_dark);
        _circleTextColor = a.getColor(R.styleable.WeekCalendarView_circleDefaultTextColor, _circleTextColor);
        a.recycle();

        setOrientation(LinearLayout.HORIZONTAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View root = inflater.inflate(R.layout.week_calendar_vp, this, true);
        viewPager = (AgcViewPager) root.findViewById(R.id.weekvp);
        leftNav = (ImageButton) root.findViewById(R.id.left_nav);
        rightNav = (ImageButton) root.findViewById(R.id.right_nav);

        leftNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = viewPager.getCurrentItem();
                if (tab > 0) {
                    tab--;
                    viewPager.setCurrentItem(tab);
                } else if (tab == 0) {
                    viewPager.setCurrentItem(tab);
                }
            }
        });

        rightNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int tab = viewPager.getCurrentItem();
                    tab++;
                    viewPager.setCurrentItem(tab);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });

        wvadapter = new WeekViewAdapter(new ArrayList());
        vpadapter = new ViewPagerAdapter(getContext(), this);
        viewPager.setAdapter(vpadapter);
        viewPager.setCurrentItem(vpadapter.getStartPosition());
    }

    /**
     * Set the last day of week
     * @param offset - Day of week int (Sunday == 1, Friday == 6)
     */
    public void setDayOfWeekEnd(int offset) {
        Resources r = getResources();
        String name = this.getContext().getPackageName();
        for (int i = 1; i < 8; i++) {
            int viewid = r.getIdentifier("day_" + i, "id", name);
            View weekitem = findViewById(viewid);
            TextView tv = (TextView) weekitem.findViewById(R.id.calendar_day_name);
            tv.setText(getDayOfWeekText((i + offset) % 7));
        }
    }

    /**
     * We leave it the responsibility of the user to give strings not exceeding
     * the size of the circle.
     *
     * @param txt
     */
    public void setCalendarInfo(List<String> txt) {
        Resources r = getResources();
        String name = this.getContext().getPackageName();
        for (int i = 1; i < 8; i++) {
            int viewid = r.getIdentifier("day_" + i, "id", name);
            View weekitem = findViewById(viewid);
            TextView tv = (TextView) weekitem.findViewById(R.id.calendar_day_info);
            tv.setText(txt.get(i - 1));
        }
    }

    public void showLastDayMarker() {
        findViewById(R.id.day_7).findViewById(R.id.record_for_day).setVisibility(VISIBLE);
    }

    public int getSavedNavWidth() {
        return _navwidth;
    }

    public void setNavEnabled(boolean f) {
        navEnabled = f;
        if (!f) {
            leftNav.setVisibility(GONE);
            rightNav.setVisibility(GONE);
        }
    }

    public void setAdapter(WeekViewAdapter wva) {
        wvadapter = wva;
        viewPager.setAdapter(vpadapter);
        viewPager.setCurrentItem(vpadapter.getStartPosition());
    }


    /**
     * The adapter for the ViewPager, which is the backbone of this view.
     * This adapter will perform most of the styling, given the data.
     * @param <T>
     */
    public class ViewPagerAdapter<T> extends PagerAdapter {
        private static final String TAG = "ViewPagerAdapter";

        private Context mContext;
        private WeekViewSwipeable mlayout;

        public ViewPagerAdapter(Context context, WeekViewSwipeable layout) {
            mlayout = layout;
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, final int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            final View v = inflater.inflate(R.layout.week_calendar, null, true);
            v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    resizeToFit(v);
                    styleFromDayrecordsData(mContext, wvadapter.getData(), position, v);
                    setDayOfWeekText(0, v);
                }
            });
            collection.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return getNumberOfWeeks(wvadapter.getData());
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Slide " + position;
        }

        /**
         * Set the last day of week
         *
         * @param offset - Day of week int (Sunday == 1, Friday == 6)
         */
        public void setDayOfWeekText(int offset, View root) {
            Resources r = mContext.getResources();
            String name = mContext.getPackageName();
            for (int i = 1; i < 8; i++) {
                int viewid = r.getIdentifier("day_" + i, "id", name);
                View weekitem = root.findViewById(viewid);
                TextView tv = (TextView) weekitem.findViewById(R.id.calendar_day_name);
                tv.setText(getDayOfWeekText((i + offset) % 7));
            }
        }


        /**
         * @param page
         * @return Days of the week, including NULL if we have no info on that day
         */
        public List<T> getDaysForFocusedWeek(int page) {
            int a = 7 * page;

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -1 * (wvadapter.getData().size() - 1));
            int dowOfFirstDay = c.get(Calendar.DAY_OF_WEEK);
            //We use this calculate the buffer; if Sunday is first day, we can use the first element. But if
            //Wed is the first day, we need to wait 3 elements before using the first dayRecord.
            //Now, DOWofFirstDAY is in Calendar format, i.e. 1-7, we need to decrement here.
            dowOfFirstDay--;

            List<T> week = new ArrayList<>();
            int indexStart = 0 + a - dowOfFirstDay;
            int indexEnd = 7 + a - dowOfFirstDay;

            while (indexStart < 0) {
                indexStart++;
                week.add(null);
            }

            int k = 0;
            while (indexEnd > wvadapter.getData().size()) {
                k++;
                indexEnd--;
            }

            for (int i = indexStart; i < indexEnd; i++) {
                List<T> mData = wvadapter.getData();
                week.add(mData.get(i));
            }

            while (k > 0) {
                k--;
            }
            return week;
        }

        /**
         * Since our view is being instantiated in the ViewPager, we have to do some layout stuff here
         *
         * @param wv
         */
        public void resizeToFit(View wv) {
            if (wv == null || mlayout == null) return;
            Resources r = wv.getResources();
            String name = wv.getContext().getPackageName();

            int width = wv.getWidth();
            int navwidth = mlayout.getSavedNavWidth();
            if (navwidth <= 0) {
                navwidth = WeekViewSwipeable.DEFAULT_NAVBUTTON_WIDTH;
            }

            // Calculate the expected dimen of each circle
            int cvwidth = (width -
                    navwidth * 2) / 7 -
                    (WeekViewSwipeable.DEFAULT_CIRCLE_LEFTMARGIN + WeekViewSwipeable.DEFAULT_CIRCLE_RIGHTMARGIN) -
                    16;

            if (cvwidth <= 0) return;

            for (int i = 1; i < 8; i++) {
                int viewid = r.getIdentifier("day_" + i, "id", name);
                View weekitem = wv.findViewById(viewid);

                CircleView cv = (CircleView) weekitem.findViewById(R.id.calendar_day_info);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(cvwidth, cvwidth);

                layoutParams.setMargins(WeekViewSwipeable.DEFAULT_CIRCLE_LEFTMARGIN,
                        WeekViewSwipeable.DEFAULT_CIRCLE_TOPMARGIN,
                        WeekViewSwipeable.DEFAULT_CIRCLE_RIGHTMARGIN,
                        WeekViewSwipeable.DEFAULT_CRICLE_BOTMARGIN);
                cv.setLayoutParams(layoutParams);
            }
        }

        /**
         * Used to populate and style each day of the WeekViewSwipeable.
         * This function calls the overriden adapter functions
         * @return
         */
        protected void styleFromDayrecordsData(final Context context, final List<T> _allDayRecords, int page, View root) {
            Resources r = root.getContext().getResources();
            String name = context.getPackageName();
            int a = 7 * page;

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -1 * (_allDayRecords.size() - 1));
            int dowOfFirstDay = c.get(Calendar.DAY_OF_WEEK);
            //We use this calculate the buffer; if Sunday is first day, we can use the first element. But if
            //Wed is the first day, we need to wait 3 elements before using the first dayRecord.
            //Now, DOWofFirstDAY is in Calendar format, i.e. 1-7, we need to decrement here.
            dowOfFirstDay--;


            for (int i = 0; i < 7; i++) {

                //i == which card we are on
                //a == offset from the page.
                //dowOfFirstDay == 0-6 where 0 is Sunday.
                //Index is the index of the dayRecord that corresponds to the given i card.
                //If it negative, then we have no record for it.
                final int index = i + a - dowOfFirstDay;

                //Get identifier for the CircleView of the given day
                int j = i + 1; //I'm as confused as you are
                int viewid = r.getIdentifier("day_" + j, "id", name);
                View weekitem = root.findViewById(viewid);
                CircleView cv = (CircleView) weekitem.findViewById(R.id.calendar_day_info);
                TextView rfd = (TextView) weekitem.findViewById(R.id.record_for_day);

                //This snippet assigns the date numbers to the weekview
                //This code uses Sunday as the last day of the week and fills in gaps
                int offset = index - (_allDayRecords.size() - 1);
                Calendar indexedDay = Calendar.getInstance();
                indexedDay.add(Calendar.DATE, offset);
                int dow = indexedDay.get(Calendar.DAY_OF_WEEK);
                int date = indexedDay.get(Calendar.DATE);
                cv.setTitleText(Integer.toString(date));
                cv.setTitleColor(_circleTextColor);
                /*The following functions style dayview using the custom adapter data*/

                //Set strokecolor
                int strokecolor = _strokecolor;
                try{
                    strokecolor = wvadapter.getStrokeColor(index);
                } catch (IndexOutOfBoundsException e){
                    //User may forget to set for days that exceed their data lists. If they do not
                    //set a condition to check for index out of bounds, they may get an exception.
                    //We gonna be a bro here and watch out for this
                }
                cv.setStrokeColor(strokecolor);

                //Set fill color
                int fillcolor = _fillcolor;
                try{
                    fillcolor = wvadapter.getFillColor(index);
                } catch (IndexOutOfBoundsException e){

                }
                cv.setFillColor(fillcolor);

                //Set title textview
                try{
                    wvadapter.getTextView(rfd, index);
                } catch (IndexOutOfBoundsException e){
                    if (index<0 || index>=wvadapter.getData().size()){
                        rfd.setText("");
                    }
                }

                //Set subtitle text
                String subtext;
                try{
                    subtext = wvadapter.getSubText(index);
                } catch (IndexOutOfBoundsException e){
                    subtext = "";
                }
                cv.setSubtitleText(subtext);

                //Finally, set the custom circleview, if they chose to override it
                wvadapter.getCircleView(cv, index);
                wvadapter.getDayLayout(weekitem, index);
            }
        }

        /**
         * @return Index of first default position in weekadapter
         */
        public int getStartPosition() {
            return getNumberOfWeeks(wvadapter.getData()) - 1;
        }
    }

    /**
     * Given a list of days, how many weeks exist between the first
     * and last days inclusive?
     */
    public static int getNumberOfWeeks(List<?> dayrecords){
        if (dayrecords == null || dayrecords.size() == 0 ){
            return 1;
        }
        int n = dayrecords.size() / 7;
        n++;
        int rem = dayrecords.size() % 7;

        Calendar c = Calendar.getInstance();
        if (c.get(Calendar.DAY_OF_WEEK) < rem){
            n++;
        }
        //This always gives us an "extra" week - so seven days starting on Sunday may give us two weeks
        return n;
    }

    public static String getDayOfWeekText(int n) {
        switch (n) {
            case 0:
                return "Sat";
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thu";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
            default:
                return Integer.toString(n);
        }
    }
}


