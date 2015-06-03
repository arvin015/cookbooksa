package com.sky.cookbooksa.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import junit.framework.Assert;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.util.Log;

public class BitmapUtil {
	private static final String TAG = "cookbooksa";

	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static byte[] getHtmlByteArray(final String url) {
		URL htmlUrl = null;     
		InputStream inStream = null;     
		try {         
			htmlUrl = new URL(url);         
			URLConnection connection = htmlUrl.openConnection();         
			HttpURLConnection httpConnection = (HttpURLConnection)connection;         
			int responseCode = httpConnection.getResponseCode();         
			if(responseCode == HttpURLConnection.HTTP_OK){             
				inStream = httpConnection.getInputStream();         
			}     
		} catch (MalformedURLException e) {               
			e.printStackTrace();     
		} catch (IOException e) {              
			e.printStackTrace();    
		} 
		byte[] data = inputStreamToByte(inStream);

		return data;
	}

	public static byte[] inputStreamToByte(InputStream is) {
		try{
			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
			int ch;
			while ((ch = is.read()) != -1) {
				bytestream.write(ch);
			}
			byte imgdata[] = bytestream.toByteArray();
			bytestream.close();
			return imgdata;
		}catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] readFromFile(String fileName, int offset, int len) {
		if (fileName == null) {
			return null;
		}

		File file = new File(fileName);
		if (!file.exists()) {
			Log.i(TAG, "readFromFile: file not found");
			return null;
		}

		if (len == -1) {
			len = (int) file.length();
		}

		Log.d(TAG, "readFromFile : offset = " + offset + " len = " + len + " offset + len = " + (offset + len));

		if(offset <0){
			Log.e(TAG, "readFromFile invalid offset:" + offset);
			return null;
		}
		if(len <=0 ){
			Log.e(TAG, "readFromFile invalid len:" + len);
			return null;
		}
		if(offset + len > (int) file.length()){
			Log.e(TAG, "readFromFile invalid file len:" + file.length());
			return null;
		}

		byte[] b = null;
		try {
			RandomAccessFile in = new RandomAccessFile(fileName, "r");
			b = new byte[len]; // 创建合�1�7�文件大小的数组
			in.seek(offset);
			in.readFully(b);
			in.close();

		} catch (Exception e) {
			Log.e(TAG, "readFromFile : errMsg = " + e.getMessage());
			e.printStackTrace();
		}
		return b;
	}

	private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;
	public static Bitmap extractThumbNail(final String path, final int height, final int width, final boolean crop) {
		Assert.assertTrue(path != null && !path.equals("") && height > 0 && width > 0);

		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			options.inJustDecodeBounds = true;
			Bitmap tmp = BitmapFactory.decodeFile(path, options);
			if (tmp != null) {
				tmp.recycle();
				tmp = null;
			}

			Log.d(TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);
			final double beY = options.outHeight * 1.0 / height;
			final double beX = options.outWidth * 1.0 / width;
			Log.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
			options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
			if (options.inSampleSize <= 1) {
				options.inSampleSize = 1;
			}

			// NOTE: out of memory error
			while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
				options.inSampleSize++;
			}

			int newHeight = height;
			int newWidth = width;
			if (crop) {
				if (beY > beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			} else {
				if (beY < beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			}

			options.inJustDecodeBounds = false;

			Log.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options.outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize);
			Bitmap bm = BitmapFactory.decodeFile(path, options);
			if (bm == null) {
				Log.e(TAG, "bitmap decode failed");
				return null;
			}

			Log.i(TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
			final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
			if (scale != null) {
				bm.recycle();
				bm = scale;
			}

			if (crop) {
				final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1, (bm.getHeight() - height) >> 1, width, height);
				if (cropped == null) {
					return bm;
				}

				bm.recycle();
				bm = cropped;
				Log.i(TAG, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
			}
			return bm;

		} catch (final OutOfMemoryError e) {
			Log.e(TAG, "decode bitmap failed: " + e.getMessage());
			options = null;
		}

		return null;
	}

	public static Bitmap getDrawable(String path, int zoom, int mItemwidth, int mItemHerght) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int mWidth = options.outWidth;
		int mHeight = options.outHeight;
		int s = 1;
		while ((mWidth / s > mItemwidth * 2 * zoom) || (mHeight / s > mItemHerght * 2 * zoom)) {
			s *= 2;
		}

		options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.ARGB_8888;
		options.inSampleSize = s;
		Bitmap bm = BitmapFactory.decodeFile(path, options);

		if (bm != null) {
			int h = bm.getHeight();
			int w = bm.getWidth();

			float ft = (float) ((float) w / (float) h);
			float fs = (float) ((float) mItemwidth / (float) mItemHerght);

			int neww = ft >= fs ? mItemwidth * zoom : (int) (mItemHerght * zoom * ft);
			int newh = ft >= fs ? (int) (mItemwidth * zoom / ft) : mItemHerght * zoom;

			float scaleWidth = ((float) neww) / w;
			float scaleHeight = ((float) newh) / h;

			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			bm = Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true);
			return bm;
		}
		return null;
	}

	public static Bitmap scalePicture(String filename, float reqWidth, float reqHeight) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filename, opts);
			int width = opts.outWidth;
			int height = opts.outHeight;
			// 缩放比例
			double ratio = 0.0;
			if (height > reqHeight || width > reqWidth) {
				if (width > height) {
					ratio = Math.round((float) height / (float) reqHeight);
				} else {
					ratio = Math.round((float) width / (float) reqWidth);
				}

				final float totalPixels = width * height;

				final float totalReqPixelsCap = reqWidth * reqHeight * 2;

				while (totalPixels / (ratio * ratio) > totalReqPixelsCap) {
					ratio++;
				}
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inSampleSize = (int) (ratio) + 1;
			newOpts.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(filename, newOpts);

		} catch (Exception e) {
			// TODO: handle exception
		}
		return compressImage(bitmap);
	}

	public static Bitmap comp(Bitmap image, float ww, float hh) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出	
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos丄1�7
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//弄1�7始读入图片，此时把options.inJustDecodeBounds 设回true亄1�7
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		//现在主流手机比较多是800*480分辨率，扄1�7以高和宽我们设置丄1�7
		//缩放比�1�7�由于是固定比例缩放，只用高或�1�7�宽其中丄1�7个数据进行计算即叄1�7
		int be = 1;//be=1表示不缩攄1�7
		if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩攄1�7
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩攄1�7
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置缩放比例
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false亄1�7
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);//压缩好比例大小后再进行质量压缄1�7
	}

	/** 
	 * 读取图片属�1�7�：旋转的角庄1�7 
	 * @param path 图片绝对路径 
	 * @return degree旋转的角庄1�7 
	 */  
	public static int readPictureDegree(String path) {  
		int degree  = 0;  
		try {  
			ExifInterface exifInterface = new ExifInterface(path);  
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
			switch (orientation) {  
			case ExifInterface.ORIENTATION_ROTATE_90:  
				degree = 90;  
				break;  
			case ExifInterface.ORIENTATION_ROTATE_180:  
				degree = 180;  
				break;  
			case ExifInterface.ORIENTATION_ROTATE_270:  
				degree = 270;  
				break;  
			}  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
		return degree;  
	} 

	/**  
	 * 旋转图片  
	 * @param angle  
	 * @param bitmap  
	 * @return Bitmap  
	 */    
	public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {    
		//旋转图片 动作     
		Matrix matrix = new Matrix();;    
		matrix.postRotate(angle);    
		// 创建新的图片     
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,    
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);    
		return resizedBitmap;    
	}    


	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这釄1�70表示不压缩，把压缩后的数据存放到baos丄1�7
		int options = 100;
		while ( baos.toByteArray().length / 1024>50) {	//循环判断如果压缩后图片是否大亄1�70kb,大于继续压缩		
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos丄1�7
			options -= 10;//每次都减射1�7
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream丄1�7
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	//转换成圆角图牄1�7
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {  
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);  
		Canvas canvas = new Canvas(output);  
		final int color = 0xff424242;  
		final Paint paint = new Paint();  
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
		final RectF rectF = new RectF(rect);   
		final float roundPx = pixels;  
		paint.setAntiAlias(true);  
		canvas.drawARGB(0, 0, 0, 0);  
		paint.setColor(color);   
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;  
	} 
}
