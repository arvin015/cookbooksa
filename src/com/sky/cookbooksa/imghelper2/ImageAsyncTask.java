package com.sky.cookbooksa.imghelper2;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 图片下载
 *
 * @author arvin.li
 */
public class ImageAsyncTask extends AsyncTask<String, Integer, Bitmap> {

    private ImageFileCache fileCache;
    private ImageMemoryCache memoryCache;
    private int width, height;
    private ImageView imageView;
    private Bitmap failBm = null, loadingBm;
    private int failImageRes = -1, loadingImageRes = -1;

    private int limitTime = 20000;//超时时间

    private Timer timer;

    private ImageAsyncTaskCallback listener;

    private int TIMEOUT = 1000;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == TIMEOUT) {
                ImageAsyncTask.this.cancel(true);

                setFailImg();

                listener.loadImageFail();
            }
        }
    };

    public ImageAsyncTask(Context context) {

        fileCache = new ImageFileCache();
        memoryCache = new ImageMemoryCache(context);
    }

    /**
     * 回调函数
     *
     * @param listener
     */
    public void setDownLoadListener(ImageAsyncTaskCallback listener) {
        this.listener = listener;
    }

    /**
     * 图片加载失败显示图片
     *
     * @param failBm
     */
    public void configLoadFailImage(Bitmap failBm) {
        this.failBm = failBm;
    }

    /**
     * 图片加载失败显示图片
     *
     * @param failImageRes
     */
    public void configLoadFailImage(int failImageRes) {
        this.failImageRes = failImageRes;
    }

    /**
     * 图片加载中显示图片
     *
     * @param loadingBm
     */
    public void configLoadingImage(Bitmap loadingBm) {
        this.loadingBm = loadingBm;
    }

    /**
     * 图片加载中显示图片
     *
     * @param loadingImageRes
     */
    public void configLoadingImage(int loadingImageRes) {
        this.loadingImageRes = loadingImageRes;
    }

    /**
     * 设置超时时间
     *
     * @param time
     */
    public void setLimitTime(int time) {
        this.limitTime = time;
    }

    /**
     * 加载图片并设置给ImageView
     *
     * @param imageView
     * @param path
     */
    public void displayImageView(ImageView imageView, String path) {

        this.imageView = imageView;

        // 从内存缓存中获取图片
        Bitmap bitmap = memoryCache.getBitmapFromCache(path);

        if (bitmap == null) {

            bitmap = fileCache.getImage(path);// 文件缓存中获取

            if (bitmap == null) {
                execute(path);// 从网络获取
//                startTimer();
            }
        }

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);

            listener.loadImageSuccess();
        }
    }

    /**
     * 加载图片并设置给ImageView
     *
     * @param imageView
     * @param path
     * @param width
     * @param height
     */
    public void displayImageView(ImageView imageView, String path, int width, int height) {
        this.width = width;
        this.height = height;

        displayImageView(imageView, path);
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();

        if (loadingBm != null) {
            imageView.setImageBitmap(loadingBm);
        }

        if (loadingImageRes != -1) {
            imageView.setImageResource(loadingImageRes);
        }
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        // TODO Auto-generated method stub

        Bitmap bitmap;

        bitmap = ImageGetFromHttp.downloadBitmap(params[0], width, height);

        if (bitmap != null) {
            fileCache.saveBitmap(bitmap, params[0]);
            memoryCache.addBitmapToCache(params[0], bitmap);
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        // TODO Auto-generated method stub
        super.onPostExecute(bitmap);

        if (bitmap != null) {

            imageView.setImageBitmap(bitmap);

            listener.loadImageSuccess();

        } else {

            setFailImg();

            listener.loadImageFail();
        }

    }

    /**
     * 设置下载失败图片
     */
    private void setFailImg() {
        if (failBm != null) {
            imageView.setImageBitmap(failBm);
        }

        if (failImageRes != -1) {
            imageView.setImageResource(failImageRes);
        }
    }

    private void startTimer() {

        cancelTimer();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(TIMEOUT);
            }
        }, limitTime);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
