package com.sky.cookbooksa.imghelper2;

/**
 * 回调函数
 * @author arvin.li
 *
 */
public interface ImageAsyncTaskListener {

	/**
	 * 图片加载成功
	 */
	public void loadImageSuccess();

	/**
	 * 图片加载失败
	 */
	public void loadImageFail();

	/**
	 * 图片加载进度
	 * @param progress
	 */
	public void loadProgress(int progress);
}
