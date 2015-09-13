package com.sky.cookbooksa.uihelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/9/13.
 */
public class WaitDialogHelper {

    private Context context;

    private ProgressDialog dialog;

    private String msgStr;

    private int waitTime = 15000;//等待自动关闭时间

    private Timer timer;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10000) {
                dismiss();

                listener.outTime();
            }
        }
    };

    public WaitDialogHelper(Context context, String msgStr) {
        this.context = context;
        this.msgStr = msgStr;

        init();
    }

    private void init() {

        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage(msgStr);
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public void show() {
        dialog.show();

        startTimer();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        cancelTimer();
    }

    private void startTimer() {
        cancelTimer();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(10000);
            }
        }, waitTime);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void setListener(IWaitDialogHelperListener listener) {
        this.listener = listener;
    }

    private IWaitDialogHelperListener listener;

    public static interface IWaitDialogHelperListener {
        void outTime();
    }
}
