package com.sky.cookbooksa.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

/**
 * 
* @Title: httpclient.java 
* @Package com.geatgdrink.util 
* @Description: post get方式获取数据
* @author zgy 
* @date 2013-9-18 上午11:02:45 
* @version V1.0
 */
public class Httpclient {
	public static final String TAG_GET = "Get";
	public static final String TAG_POST = "Post";
	public static final int HTTP_200 = 200;
	
	/**
	 * 
	 * @param u
	 * @param maps
	 * @param timeout
	 * @return -8 获取数据错误 -9链接超时
	 * @throws Exception
	 */
	public static String requestByPost(String u,Map<String, String> maps,int timeout) throws Exception {
		String result = null; 
        URL url = null; 
        HttpURLConnection connection = null; 
        InputStreamReader in = null; 
        String strs = "";
        
            url = new URL(u); 
            connection = (HttpURLConnection) url.openConnection(); 
            // 设置连接超时时间
            connection.setConnectTimeout(timeout * 1000);
            connection.setReadTimeout(timeout * 1000);
            connection.setDoInput(true); 
            connection.setDoOutput(true); 
            connection.setRequestMethod("POST"); 
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
            connection.setRequestProperty("Charset", "utf-8"); 
        try { 
            DataOutputStream dop = new DataOutputStream( 
                    connection.getOutputStream());
            
            for (Map.Entry<String, String> param : maps.entrySet()) {
            	strs += param.getKey()+"="+ URLEncoder.encode(param.getValue(), "utf-8")+"&";
            }
            strs = strs.substring(0, (strs.length()-1));
//            dop.writeBytes("t="+URLEncoder.encode("哈哈", "utf-8")); 
            dop.writeBytes(strs);
            dop.flush(); 
            dop.close(); 
  
            in = new InputStreamReader(connection.getInputStream()); 
            BufferedReader bufferedReader = new BufferedReader(in); 
            StringBuffer strBuffer = new StringBuffer(); 
            String line = null; 
            while ((line = bufferedReader.readLine()) != null) { 
                strBuffer.append(line); 
            } 
            result = strBuffer.toString(); 
        } catch (SocketTimeoutException e) {
        	result = "-9";
	    } catch (Exception e) { 
            result = "-8";
	    	//e.printStackTrace(); 
        } finally { 
            if (connection != null) { 
                connection.disconnect(); 
            } 
            if (in != null) { 
                try { 
                    in.close(); 
                } catch (IOException e) { 
                    e.printStackTrace(); 
                } 
            } 
  
        } 
        return result; 
    }

	/**
	 * 
	 * @param u
	 * @param timeout
	 * @return -8 获取数据错误 -9链接超时
	 * @throws Exception
	 */
	public static String requestByGet(String u,int timeout) throws Exception {
		String path = u;
		// 新建丄1�7个URL对象
		URL url = new URL(path);
		// 打开丄1�7个HttpURLConnection连接
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		// 设置连接超时时间
		urlConn.setConnectTimeout(timeout * 1000);
		urlConn.setReadTimeout(timeout * 1000);
		// 弄1�7始连掄1�7
		urlConn.connect();
		try { 
			// 判断请求是否成功
			if (urlConn.getResponseCode() == HTTP_200) {
				// 获取返回的数捄1�7
				byte[] data = readStream(urlConn.getInputStream());
				Log.i(TAG_GET, "Get方式请求成功，返回数据如下：");
				Log.i(TAG_GET, new String(data, "UTF-8"));
				// 关闭连接
				urlConn.disconnect();
				return new String(data, "UTF-8");
			} else {
				Log.i(TAG_GET, "Get方式请求失败");
				// 关闭连接
				urlConn.disconnect();
				return "-8";
			}
		}
		catch (SocketTimeoutException e) {
        	return "-9";
	    }
	}
	// 获取连接返回的数捄1�7
	private static byte[] readStream(InputStream inputStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		byte[] data = baos.toByteArray();
		inputStream.close();
		baos.close();
		return data;
	}

	public static Bitmap loadImageFromUrl(String urlStr){
		Bitmap bitmap = null;
		try
		{
			 URL url = new URL(urlStr);
	         URLConnection conn = url.openConnection();
	         conn.connect();
	         InputStream in = conn.getInputStream();
	         BufferedInputStream bis= new BufferedInputStream(in);
	         
	         BitmapFactory.Options options=new Options();
			    options.inDither=false;    /*不进行图片抖动处琄1�7*/
			    options.inPreferredConfig=null;  /*设置让解码器以最佳方式解砄1�7*/
			    options.inSampleSize=5;          /*图片长宽方向缩小倍数*/
	         
	         bitmap = BitmapFactory.decodeStream(bis,null,options);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return bitmap;
	}
}
