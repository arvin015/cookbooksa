package com.sky.cookbooksa.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.sky.cookbooksa.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by arvin.li on 2015/8/11.
 */
public class ProgressDialogUtil {

    private ProgressDialog dialog;

    private static ProgressDialogUtil dialogUtil;

    private final String dialogText = "加载中...";

    private Timer timer;//计时器

    private final int totalTime = 60000;//60秒后自动关闭Dialog

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 2000) {
                closeDialog();
            }
        }
    };

    private ProgressDialogUtil(Context context) {

        dialog = new ProgressDialog(context, R.style.CustomDialogTheme);
        dialog.setCancelable(false);
        dialog.setMessage(dialogText);
    }

    public static ProgressDialogUtil getInstance(Context context) {

        if (dialogUtil == null) {
            dialogUtil = new ProgressDialogUtil(context);
        }

        return dialogUtil;
    }

    /**
     * 设置是否可点击屏幕隐藏
     *
     * @param enable
     */
    public void setCancelable(boolean enable) {
        dialog.setCancelable(enable);
    }

    /**
     * 设置提示信息
     *
     * @param text
     */
    public void setDialogText(String text) {
        dialog.setMessage(text);
    }

    /**
     * 显示
     */
    public void showDialog() {
        dialog.show();

        startTimer();
    }

    /**
     * 隐藏
     */
    public void closeDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        cancelTimer();
    }

    /**
     * 开始计时
     */
    private void startTimer() {
        cancelTimer();

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(2000);
            }
        }, totalTime);
    }

    /**
     * 清除计时
     */
    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
