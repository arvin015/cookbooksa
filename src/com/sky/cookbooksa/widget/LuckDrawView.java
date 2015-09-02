package com.sky.cookbooksa.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.sky.cookbooksa.R;

/**
 * Created by arvin.li on 2015/8/19.
 */
public class LuckDrawView extends FrameLayout {

    private Context context;

    private ImageView rotateImg, pointImg;

    private int totalAngle;//总共需旋转度数

    private int defaultAngle = 1080;//默认需最小旋转角度

    private int perAngle = 60;//每个选项区度数

    private int areaId;//目标选区Id

    private int playTime = 3000;//旋转播放时间

    public LuckDrawView(Context context) {
        this(context, null);
    }

    public LuckDrawView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckDrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;

        init();
    }

    /**
     * 初始化
     */
    private void init() {

        rotateImg = createImageView(R.drawable.bg_wheel);

        addView(rotateImg);

        pointImg = createImageView(R.drawable.point);

        addView(pointImg);
    }

    /**
     * 设置旋转、指针图片
     *
     * @param rotateBm
     * @param pointBm
     */
    public void setRotateAndPointBm(Bitmap rotateBm, Bitmap pointBm) {

        if (rotateBm != null) {
            rotateImg.setImageBitmap(rotateBm);
        }

        if (pointBm != null) {
            pointImg.setImageBitmap(pointBm);
        }

    }

    /**
     * 设置旋转、指针图片
     *
     * @param rotateId
     * @param pointId
     */
    public void setRotateAndPointDrawable(int rotateId, int pointId) {
        Bitmap rotateBm = BitmapFactory.decodeResource(context.getResources(), rotateId);
        Bitmap pointBm = BitmapFactory.decodeResource(context.getResources(), pointId);

        setRotateAndPointBm(rotateBm, pointBm);
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
     * 设置旋转播放时间
     *
     * @param playTime --- 毫秒
     */
    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    /**
     * 开始旋转
     *
     * @param areaId
     */
    public void startTurn(int areaId) {

        this.areaId = areaId;
        this.totalAngle = defaultAngle + areaId * perAngle;

        playAnim(rotateImg, 0, totalAngle);
    }

    /**
     * 创建ImageView
     *
     * @param drawableId
     * @return
     */
    private ImageView createImageView(int drawableId) {
        ImageView imageView = new ImageView(context);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        imageView.setImageResource(drawableId);

        imageView.setLayoutParams(params);

        return imageView;
    }

    /**
     * 播放旋转动画
     *
     * @param view
     * @param from
     * @param to
     */
    private void playAnim(View view, float from, float to) {
        RotateAnimation rotateAnimation = new RotateAnimation(from, to,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(playTime);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());
        rotateAnimation.setFillAfter(true);

        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                listener.luckDrawEnd(areaId);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(rotateAnimation);
    }

    private ILuckDrawListener listener;

    /**
     * 设置回调函数
     *
     * @param listener
     */
    public void setListener(ILuckDrawListener listener) {
        this.listener = listener;
    }

    /**
     * 回调函数
     */
    public interface ILuckDrawListener {
        /**
         * 旋转结束
         */
        void luckDrawEnd(int areaId);
    }
}
