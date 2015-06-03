package com.sky.cookbooksa.utils;

import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
/**
 * 
 * @author arvin.li
 *
 */
public class ImageAsyncTask extends AsyncTask<String, Integer, Bitmap>{

	public ImageAsyncTask(ImageAsyncTaskListener listener){
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);

		listener.returnProgress(values[0]);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		// TODO Auto-generated method stub

		Bitmap bm = null;

		try {
			URL url = new URL(params[0]);

			HttpURLConnection http = (HttpURLConnection) url.openConnection();

			bm = BitmapFactory.decodeStream(http.getInputStream());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bm;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		listener.returnBitmap(result);
	}

	private ImageAsyncTaskListener listener;

	public interface ImageAsyncTaskListener{

		public void returnBitmap(Bitmap bitmap);

		public void returnProgress(int progress);
	}

}
