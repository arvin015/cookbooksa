package com.sky.cookbooksa.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.sky.cookbooksa.R;

/**
 * Created by arvin.li on 2015/7/23.
 */
public class TurntableView extends View {

    private Context context;

    private Bitmap bgBm, pointBm;

    private boolean isTurning = false;

    private int turnAngle;//已旋转度数

    private int totalAngle;//总共需旋转度数

    private int defaultAngle = 1080;//默认需最小旋转角度

    private int perAngle = 60;//每个选项区度数

    private int areaId;//目标选区Id

//    private GestureDetector mGestureDetector;

//    private RectF mRectF;//点击开始目标区域

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

    /**
     * 初始化
     */
    private void init() {

        bgBm = BitmapFactory.decodeResource(getResources(), R.drawable.bg_wheel);
        pointBm = BitmapFactory.decodeResource(getResources(), R.drawable.point);

        /*//手势
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {//点击up事件

                clickUpHandle(e);

                return super.onSingleTapUp(e);
            }
        });*/
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

            if (Math.ceil(totalAngle - turnAngle) <= 6) {

                listener.luckDrawEnd(areaId);

                isTurning = false;
            }

            canvas.save();

            canvas.rotate(turnAngle, getWidth() / 2, getHeight() / 2);
            canvas.drawBitmap(bgBm, 0, 0, null);

            canvas.restore();

            turnAngle += 6;

        } else {
            canvas.drawBitmap(bgBm, 0, 0, null);
        }

        canvas.drawBitmap(pointBm, 0, 0, null);

        if (isTurning) {
            invalidate();
        }
    }

    /**
     * 设置旋转数据
     *
     * @param defaultAngle
     * @param perAngle
     */
    public void setDefaultAngle(int defaultAngle, int perAngle) {
        this.defaultAngle = defaultAngle;
        this.perAngle = perAngle;
    }

    /**
     * 开始旋转
     *
     * @param areaId
     */
    public void startTurn(int areaId) {

        this.areaId = areaId;
        this.totalAngle = defaultAngle + areaId * perAngle;

        turnAngle = 0;

        isTurning = true;

        invalidate();
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {

        mGestureDetector.onTouchEvent(event);//将Touch事件交给手势处理

        return true;
    }*/

   /* */

    /**
     * 点击处理
     *
     * @param e
     *//*
    private void clickUpHandle(MotionEvent e) {

        if (isTurning) {
            return;
        }

        totalAngle = defaultAngle + (RandomUtil.randomNum(360 / perAngle) + 1) * 60;

        //创建一个点击有效区域
        if (mRectF == null) {
            mRectF = new RectF(getWidth() / 2 - 40, getHeight() / 2 - 40,
                    getWidth() / 2 + 40, getHeight() / 2 + 40);
        }

        if (mRectF.contains(e.getX(), e.getY())) {

            isTurning = true;

            turnAngle = 0;

            invalidate();
        }
    }*/

    private ITurntableListener listener;

    /**
     * 设置回调函数
     *
     * @param listener
     */
    public void setListener(ITurntableListener listener) {
        this.listener = listener;
    }

    /**
     * 回调函数
     */
    public interface ITurntableListener {
        /**
         * 旋转结束
         */
        void luckDrawEnd(int areaId);
    }
}
