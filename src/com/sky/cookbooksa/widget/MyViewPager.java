package com.sky.cookbooksa.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by arvin.li on 2015/8/31.
 */
public class MyViewPager extends ViewPager {

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //简单但重要一步，ViewPager获得touch焦点时候，阻止父层ScorllView及祖父层SlidingMenu的拦截
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean ret = super.dispatchTouchEvent(ev);
        if (ret) {
            requestDisallowInterceptTouchEvent(true);
        }
        return ret;
    }
}
