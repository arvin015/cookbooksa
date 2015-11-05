package com.sky.cookbooksa.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.sky.cookbooksa.R;

/**
 * Created by arvin.li on 2015/10/27.
 */
public class SystemManager {

    /**
     * 设置状态栏样式
     *
     * @param act
     */
    public static void initSystemBar(Activity act) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(act, true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(act);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.main_yellow);// 必须使用颜色资源
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity act, boolean on) {

        Window win = act.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();

        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
