package com.sky.cookbooksa.uihelper;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sky.cookbooksa.MainActivity;
import com.sky.cookbooksa.R;
import com.sky.cookbooksa.imghelper2.ImageAsyncTask;
import com.sky.cookbooksa.imghelper2.ImageAsyncTaskCallback;
import com.sky.cookbooksa.utils.DisplayUtil;
import com.sky.cookbooksa.utils.ToastUtil;

public class ImageDialogHelper {

    private MainActivity act;

    private Dialog dialog;

    private String path;

    private ImageAsyncTask imageAsyncTask;

    public ImageDialogHelper(MainActivity act, String path) {

        this.act = act;
        this.path = path;

        imageAsyncTask = new ImageAsyncTask(act);
        imageAsyncTask.configLoadingImage(R.drawable.photo_loading);
        imageAsyncTask.configLoadFailImage(R.drawable.photo_loading);

        init();
    }

    private void init() {
        // TODO Auto-generated method stub

        dialog = new Dialog(act, R.style.CustomDialogTheme);

        LayoutParams params = new LayoutParams(DisplayUtil.screenWidth,
                DisplayUtil.screenHeight);

        View view = LayoutInflater.from(act).inflate(R.layout.image_dialog, null);

        dialog.setContentView(view, params);

        ImageView imageView = (ImageView) dialog.findViewById(R.id.image);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.loading);

        imageAsyncTask.setDownLoadListener(new ImageAsyncTaskCallback() {

            @Override
            public void loadImageSuccess() {
                // TODO Auto-generated method stub
                super.loadImageSuccess();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void loadImageFail() {
                super.loadImageFail();

                ToastUtil.toastShort(act, "图片下载失败！");

                progressBar.setVisibility(View.GONE);
            }
        });
        imageAsyncTask.displayImageView(imageView, path);
    }

    public void showDialog() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void closeDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
