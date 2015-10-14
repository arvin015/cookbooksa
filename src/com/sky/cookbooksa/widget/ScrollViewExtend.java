package com.sky.cookbooksa.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * 能够兼容ViewPager的ScrollView
 *
 * @author arvin
 * @Description: 解决了ViewPager在ScrollView中的滑动反弹问题
 * @Description: 可监听ScrollView滑动停止事件
 */
public class ScrollViewExtend extends ScrollView {

    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;

    private int lastY = 0;
    private int touchEventId = -9983761;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            View scroller = (View) msg.obj;
            if (msg.what == touchEventId) {
                if (lastY == scroller.getScrollY()) {
                    if (listener != null) {
                        listener.scrollStop();
                    }
                } else {
                    handler.sendMessageDelayed(handler.obtainMessage(touchEventId, scroller), 100);
                    lastY = scroller.getScrollY();
                }
            }
        }
    };

    public ScrollViewExtend(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();

                final float curY = ev.getY();
                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                if (xDistance > yDistance) {
                    return false;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            handler.sendMessageDelayed(handler.obtainMessage(touchEventId, this), 5);
        }
        return super.onTouchEvent(ev);
    }

    private IScrollViewListener listener;

    /**
     * 设置滑动事件监听器
     *
     * @param listener
     */
    public void setListener(IScrollViewListener listener) {
        this.listener = listener;
    }

    /**
     * 滑动接口
     */
    public interface IScrollViewListener {
        /**
         * 滑动停止事件
         */
        void scrollStop();
    }

}