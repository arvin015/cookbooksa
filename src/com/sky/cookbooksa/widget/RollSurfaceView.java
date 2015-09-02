package com.sky.cookbooksa.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sky.cookbooksa.utils.DisplayUtil;

/**
 * SurfaceView使用例子
 * Created by arvin.li on 2015/8/21.
 */
public class RollSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private boolean needRun = true;//是否需要滚动

    private MyThread mThread;

    private SurfaceHolder surfaceHolder;

    private StringBuffer stringBuffer;

    private float rollX, rollY;

    private Paint paint;

    public RollSurfaceView(Context context) {
        this(context, null);
    }

    public RollSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RollSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    /**
     * 初始化
     */
    private void init() {

        stringBuffer = new StringBuffer("以下是中奖用户：130xxxxxxxx、130xxxxxxxx、130xxxxxxxx、130xxxxxxxx、130xxxxxxxx");


        surfaceHolder = getHolder();

        surfaceHolder.addCallback(this);

    }

    /**
     * 添加文本内容
     *
     * @param text
     */
    public void addText(String text) {
        stringBuffer.append("、" + text);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        needRun = true;

        mThread = new MyThread(surfaceHolder);

        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        needRun = false;

        mThread = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 线程
     */
    class MyThread extends Thread {

        private SurfaceHolder surfaceHolder;

        public MyThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        @Override
        public void run() {
            super.run();

            while (needRun) {

                draw();
            }
        }

        /**
         * 绘画
         */
        private void draw() {
            Canvas canvas = null;

            try {
                synchronized (surfaceHolder) {

                    //锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了
                    canvas = surfaceHolder.lockCanvas();

                    canvas.drawColor(Color.parseColor("#DBDBDB"));

                    paint = new Paint();
                    paint.setColor(Color.parseColor("#F5AE38"));
                    paint.setTextSize(DisplayUtil.sp2px(18));
                    paint.setAntiAlias(true);

                    rollY = getMeasuredHeight() - getFontHeight() / 2;

                    canvas.drawText(stringBuffer.toString(), rollX, DisplayUtil.dip2px(25), paint);

                    rollX -= 2;

                    float totalWidth = getStringWidth(stringBuffer.toString());

                    if (Math.abs(rollX) >= totalWidth) {
                        rollX = DisplayUtil.screenWidth;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);//结束锁定画图，并提交改变
                }

            }
        }
    }

    /**
     * 获取字符串宽
     *
     * @param str
     * @return
     */
    private int getStringWidth(String str) {

        return (int) paint.measureText(str);

    }


    /**
     * 获取字体高
     *
     * @return
     */
    private int getFontHeight() {

        Paint.FontMetrics fm = paint.getFontMetrics();

        return (int) (Math.ceil(fm.descent - fm.top) + 2);

    }
}
