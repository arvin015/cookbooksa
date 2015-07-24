package com.sky.cookbooksa.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.sky.cookbooksa.R;

/**
 * Created by arvin.li on 2015/7/23.
 */
public class TurntableView extends View {

    private Context context;

    private Bitmap bgBm, pointBm;

    private boolean isTurning = true;

    private int turnAngle;

    private int totalAngle = 760;

    private GestureDetector mGestureDetector;

    public TurntableView(Context context) {
        this(context, null);
    }

    public TurntableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TurntableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;

        init();
    }

    private void init() {

        bgBm = BitmapFactory.decodeResource(getResources(), R.drawable.bg_wheel);
        pointBm = BitmapFactory.decodeResource(getResources(), R.drawable.point);

        //手势
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {//点击up事件

                clickUpHandle(e);

                return super.onSingleTapUp(e);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (bgBm != null) {
            setMeasuredDimension(bgBm.getWidth(), bgBm.getHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isTurning) {

            if (Math.ceil(totalAngle - turnAngle) <= 3) {
                isTurning = false;
            }

            canvas.save();

            canvas.rotate(turnAngle, getWidth() / 2, getHeight() / 2);
            canvas.drawBitmap(bgBm, 0, 0, null);

            canvas.restore();

            turnAngle += 3;

        } else {
            canvas.drawBitmap(bgBm, 0, 0, null);
        }

        canvas.drawBitmap(pointBm, 0, 0, null);

        if (isTurning) {
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mGestureDetector.onTouchEvent(event);//将Touch事件交给手势处理

        return true;
    }

    private void clickUpHandle(MotionEvent e) {

        if (isTurning) {
            return;
        }

        //创建一个点击有效区域
        RectF mRectF = new RectF(getWidth() / 2 - 40, getHeight() / 2 - 40,
                getWidth() / 2 + 40, getHeight() / 2 + 40);

        if (mRectF.contains(e.getX(), e.getY())) {

            isTurning = true;

            turnAngle = 0;

            invalidate();
        }
    }
}
