package com.sky.cookbooksa.imghelper2;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
/**
 * 图片下载
 * @author arvin.li
 *
 */
public class ImageAsyncTask extends AsyncTask<String, Integer, Bitmap>{

	private ImageFileCache fileCache;
	private ImageMemoryCache memoryCache;
	private int width, height;
	private ImageView imageView;
	private Bitmap failBm = null, loadingBm;
	private int failImageRes = -1, loadingImageRes = -1;

	private ImageAsyncTaskCallback listener;

	public ImageAsyncTask(Context context){

		fileCache = new ImageFileCache();
		memoryCache = new ImageMemoryCache(context);
	}

	/**
	 * 回调函数
	 * @param listener
	 */
	public void setDownLoadListener(ImageAsyncTaskCallback listener){
		this.listener = listener;
	}

	/**
	 * 图片加载失败显示图片
	 * @param failBm
	 */
	public void configLoadFailImage(Bitmap failBm){
		this.failBm = failBm;
	}

	/**
	 * 图片加载失败显示图片
	 * @param failImageRes
	 */
	public void configLoadFailImage(int failImageRes){
		this.failImageRes = failImageRes;
	}

	/**
	 * 图片加载中显示图片
	 * @param loadingBm
	 */
	public void configLoadingImage(Bitmap loadingBm){
		this.loadingBm = loadingBm;
	}

	/**
	 * 图片加载中显示图片
	 * @param loadingImageRes
	 */
	public void configLoadingImage(int loadingImageRes){
		this.loadingImageRes = loadingImageRes;
	}

	/**
	 * 加载图片并设置给ImageView
	 * @param imageView
	 * @param path
	 */
	public void displayImageView(ImageView imageView, String path){

		this.imageView = imageView;

		execute(path);
	}

	/**
	 * 加载图片并设置给ImageView
	 * @param imageView
	 * @param path
	 * @param width
	 * @param height
	 */
	public void displayImageView(ImageView imageView, String path, int width, int height){
		this.width = width;
		this.height = height;

		displayImageView(imageView, path);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

		if(loadingBm != null){
			imageView.setImageBitmap(loadingBm);
		}

		if(loadingImageRes != -1){
			imageView.setImageResource(loadingImageRes);
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);

		listener.loadProgress(values[0]);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		// TODO Auto-generated method stub

		// 从内存缓存中获取图片
		Bitmap bitmap = memoryCache.getBitmapFromCache(params[0]);
		if (bitmap == null) {
			// 文件缓存中获取
			bitmap = fileCache.getImage(params[0]);
			if (bitmap == null) {
				// 从网络获取
				bitmap = ImageGetFromHttp.downloadBitmap(params[0], width, height);
				if (bitmap != null) {
					fileCache.saveBitmap(bitmap, params[0]);
					memoryCache.addBitmapToCache(params[0], bitmap);
				}
			} else {
				// 添加到内存缓存
				memoryCache.addBitmapToCache(params[0], bitmap);
			}
		}

		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		if(result != null){

			imageView.setImageBitmap(result);

			listener.loadImageSuccess();

		}else{

			if(failBm != null){
				imageView.setImageBitmap(failBm);
			}

			if(failImageRes != -1){
				imageView.setImageResource(failImageRes);
			}

			listener.loadImageFail();
		}

	}
}
