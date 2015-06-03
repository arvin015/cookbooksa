package com.sky.cookbooksa.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 
* @Title: NetworkUtil.java 
* @Package com.fullteem.utils 
* @Description: Android网络状�1�7�工具类，使用此类获取网络状态信恄1�7 
* @author Alexclin   
* @date 2013-4-10 下午5:59:23 
* @version V1.0
 */
public class NetworkUtil {
	private static final int CMNET = 3;
	private static final int CMWAP = 2;
	private static final int WIFI = 1;

	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @param context
	 * @return 当前网络类型，或无连接为-1
	 */
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	/**
	 * 
	 * @author sky
	 * 
	 *         Email vipa1888@163.com
	 * 
	 *         QQ:840950105
	 * 
	 *         获取当前的网络状怄1�7 -1：没有网组1�7 1：WIFI网络2：wap网络3：net网络
	 * 
	 * @param context
	 * 
	 * @return
	 */

	public static int getAPNType(Context context) {
		int netType = -1;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}

		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			Log.e("networkInfo.getExtraInfo()",
					"networkInfo.getExtraInfo() is "
							+ networkInfo.getExtraInfo());
			if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
				netType = CMNET;
			}
			else {
				netType = CMWAP;
			}
		}
		else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = WIFI;
		}
		return netType;

	}

	/**
	 * 新建丄1�7个网络状态监听器并注冄1�7
	 * 
	 * @param context
	 *            上下斄1�7
	 * @param callback
	 *            网络改变时的回调
	 * @return 被注册的广播接收噄1�7
	 */
	public static BroadcastReceiver registerNetworkCallback(Context context,
			NetworkChangedCallback callback) {
		BroadcastReceiver connectionReceiver = new ConnectionReceiver(callback);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		context.registerReceiver(connectionReceiver, intentFilter);
		return connectionReceiver;
	}

	public static class ConnectionReceiver extends BroadcastReceiver {
		private NetworkChangedCallback callback;

		public ConnectionReceiver(NetworkChangedCallback callback) {
			super();
			this.callback = callback;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mobNetInfo.isConnected() || wifiNetInfo.isConnected()) {
				callback.onNetworkConnected(connectMgr);
			} else {
				callback.onNetworkUnconnected(connectMgr);
			}
		}
	}

	public interface NetworkChangedCallback {
		public void onNetworkConnected(ConnectivityManager cm);

		public void onNetworkUnconnected(ConnectivityManager cm);
	}
}
