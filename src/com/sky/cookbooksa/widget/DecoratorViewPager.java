package com.sky.cookbooksa.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * viewpager和listview相互冲突
 * 在dispatchTouchEvent分发事件中使用父类的方法getParent().requestDisallowInterceptTouchEvent(true)
 * 当requestDisallowInterceptTouchEvent为true，表示父view不拦截子view的touch事件
 *
 * @author arvin.li
 */
public class DecoratorViewPager extends ViewPager {

    //滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;

    public DecoratorViewPager(Context context) {
        this(context, null);
    }

    public DecoratorViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();

                //父类不拦截ViewPager的Touch事件
                getParent().requestDisallowInterceptTouchEvent(true);

                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();

                final float curY = ev.getY();
                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                if (xDistance > yDistance) {//左右滑动，父类不拦截ViewPager的Touch事件
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {//上下滑动，父类拦截ViewPager的Touch事件
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
        }

        return super.dispatchTouchEvent(ev);
    }

}
