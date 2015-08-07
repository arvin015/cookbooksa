package com.sky.cookbooksa.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.sky.cookbooksa.R;

/**
 * Created by arvin.li on 2015/7/22.
 */
public class RadarScanView extends View {

    protected Context context;

    private Bitmap bgBm, scanBm;

    private float rotateValue = 0;

    public RadarScanView(Context context) {
        this(context, null);
    }

    public RadarScanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;

        init();
    }

    private void init() {

        bgBm = BitmapFactory.decodeResource(getResources(), R.drawable.gplus_search_bg);
        scanBm = BitmapFactory.decodeResource(getResources(), R.drawable.gplus_search_args);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bgBm, getWidth() / 2 - bgBm.getWidth() / 2,
                getHeight() / 2 - bgBm.getHeight() / 2, null);

        canvas.save();
        canvas.rotate(rotateValue, getWidth() / 2, getHeight() / 2);

        canvas.drawBitmap(scanBm, getWidth() / 2 - scanBm.getWidth(),
                getHeight() / 2, null);
        canvas.restore();

        rotateValue += 3;

        invalidate();

    }

}
