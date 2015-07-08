package com.sky.cookbooksa.utils;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author arvin.li
 *
 */
public class UploadUtil {  
	private static final int TIME_OUT = 20 * 1000; // 超时时间  
	private static final String CHARSET = "utf-8"; // 设置编码  
	private String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成  
	private String PREFIX = "--", LINE_END = "\r\n";  
	private String CONTENT_TYPE = "multipart/form-data"; // 内容类型  
	private final static String LINEND = "\r\n"; 
	/** 
	 * 上传文件到服务器 
	 * @param file 需要上传的文件名
	 * @param requestURL 请求的rul 
	 * @return 返回响应的内容 
	 */  
	public JSONObject uploadFile(String file, HashMap<String, String> maps, String requestURL) {   

		JSONObject json = null;

		try {  
			String urlString = null;
			urlString = requestURL;
			URL url = new URL(urlString);  
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
			conn.setReadTimeout(TIME_OUT);  
			conn.setConnectTimeout(TIME_OUT);  
			conn.setDoInput(true); // 允许输入流  
			conn.setDoOutput(true); // 允许输出流  
			conn.setUseCaches(false); // 不允许使用缓存  
			conn.setRequestMethod("POST"); // 请求方式  
			conn.setRequestProperty("Charset", CHARSET); // 设置编码  
			conn.setRequestProperty("connection", "keep-alive");  
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="+ BOUNDARY);  

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());  
			StringBuffer sb = new StringBuffer(); 

			sb.append(PREFIX);  
			sb.append(BOUNDARY);  
			sb.append(LINE_END);  
			/** 
			 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件 
			 * filename是文件的名字，包含后缀名 
			 */  
			sb.append("Content-Disposition: form-data; name=\"image\"; filename=\""  
					+ file.substring(file.lastIndexOf("/")+1) + "\"" + LINE_END);  
			sb.append("Content-Type: application/octet-stream; charset="  
					+ CHARSET + LINE_END);  
			sb.append(LINE_END);  
			dos.write(sb.toString().getBytes());  
			InputStream is = new FileInputStream(file);
			byte[] bytes = new byte[1024];  
			int len = 0;  
			while ((len = is.read(bytes)) != -1) {  
				dos.write(bytes, 0, len);  
			}  
			is.close();  
			dos.write(LINE_END.getBytes());
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)  
					.getBytes();  
			dos.write(end_data); 
			String bulidText = bulidFormText(maps);
			dos.write(bulidText.getBytes());
			dos.flush();  
			/** 
			 * 获取响应码 200=成功 当响应成功，获取响应的流 
			 */ 
			if (conn.getResponseCode() == 200) {  
				InputStream input = conn.getInputStream();  
				StringBuffer sb1 = new StringBuffer();  
				int ss;  
				while ((ss = input.read()) != -1) {  
					sb1.append((char) ss);  
				}  
				String result = sb1.toString();

				try {
					json = new JSONObject(result);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {  
			}  
		} catch (MalformedURLException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  

		return json;  
	}  

	/** 
	 * 封装表单文本数据 
	 * @param paramText 
	 * @return 
	 */  
	private String bulidFormText(Map<String,String> paramText){  
		if(paramText==null || paramText.isEmpty()) return "";  
		StringBuffer sb = new StringBuffer("");  
		for(java.util.Map.Entry<String, String> entry : paramText.entrySet()){   
			sb.append(PREFIX).append(BOUNDARY).append(LINEND);  
			sb.append("Content-Disposition:form-data;name=\""  
					+ entry.getKey() + "\"" + LINEND);  
			sb.append(LINEND);  
			sb.append(entry.getValue());  
			sb.append(LINEND);  
		}  
		return sb.toString();  
	}  

}  