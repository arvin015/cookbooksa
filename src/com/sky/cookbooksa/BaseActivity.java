package com.sky.cookbooksa;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.sky.cookbooksa.utils.ExitApplication;
import com.sky.cookbooksa.utils.NetworkUtil;
import com.sky.cookbooksa.utils.SystemBarTintManager;
import com.sky.cookbooksa.utils.ToastUtil;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;

public class BaseActivity extends Activity {

    protected Context context;

    protected FinalHttp fh;

    protected FinalBitmap fb;

    protected ProgressDialog pb;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setSystemBarTintColor();

        init();
    }

    /**
     * 设置状态栏颜色
     */
    private void setSystemBarTintColor() {

        setTranslucentStatus(true);

        //sdk 4.4以上可设置状态栏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.main_yellow));
        }
    }

    /**
     * 设置状态栏样式
     *
     * @param on
     */
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void cancleTranslucentStatus() {
        setTranslucentStatus(false);
    }

    private void init() {
        // TODO Auto-generated method stub

        ExitApplication.getInstance(context).addActivity(this);
        if (!NetworkUtil.isNetworkConnected(this)) {
            ToastUtil.toastShort(this, "当前网络未连接，请检测网络！");
        }

        context = this;

        fh = new FinalHttp();

        fb = FinalBitmap.create(this);
        fb.configLoadfailImage(R.drawable.photo_loading);
        fb.configLoadingImage(R.drawable.photo_loading);
    }

    protected void loading(String msg) {
        pb = new ProgressDialog(context);
        pb.setCancelable(true);
        pb.setMessage(msg);
        pb.show();
    }

    protected void loadMissed() {
        if (pb != null && pb.isShowing()) {
            pb.dismiss();
        }
    }

    protected void intentHandle(Class<?> to, Bundle bundle, boolean needFinish) {
        if (intent == null) {
            intent = new Intent();
        }

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        intent.setClass(this, to);

        startActivity(intent);

        if (needFinish) {
            finish();
        }
    }

    //toggle keyboard
    protected void toggleKeyboard(EditText edit, boolean needOpen) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (needOpen) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK & event.getRepeatCount() == 0) {
        }

        return super.onKeyDown(keyCode, event);
    }

    //finish current Activity
    protected void back() {
        finish();
    }

    //exit app
    protected void exitApp() {
        ExitApplication.getInstance(context).exit();
    }
}
