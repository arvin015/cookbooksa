package com.sky.cookbooksa.utils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class PositionManager implements Runnable{

	private static PositionManager posManager;

	private LocationManagerProxy locManager;

	private MyAMapLocationListener mapListener;

	private AMapLocation aMapLocation;

	private int SUCCESS_CODE = 001;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == SUCCESS_CODE){
				
				callback.getPosition(aMapLocation.getProvince() + " " + aMapLocation.getCity() 
						+ " " +aMapLocation.getDistrict());

				destoryLocation();
			}
		};
	};

	private PositionManager(Context context){

		locManager = LocationManagerProxy.getInstance(context);

		mapListener = new MyAMapLocationListener();
	}

	public static PositionManager getInstance(Context context){

		if(posManager == null){
			posManager = new PositionManager(context);

		}

		return posManager;
	}

	public void requestPosition(IPositionCallback callback){

		this.callback = callback;

		new Thread(new Runnable(){

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				locManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 0, 0, mapListener);
			}

		}).start();

		handler.postDelayed(this, 12000);//12内还未定位停止定位
	}

	@SuppressWarnings("deprecation")
	public void destoryLocation(){
		if(locManager != null){
			locManager.removeUpdates(mapListener);
			locManager.destory();
		}
	}

	class MyAMapLocationListener implements AMapLocationListener{

		@Override
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLocationChanged(AMapLocation location) {
			// TODO Auto-generated method stub

			aMapLocation = location;

			if(location != null){

				Log.d("print", String.format("position--->%s", location.getExtras().getString("desc")));

				handler.sendEmptyMessage(SUCCESS_CODE);
			}	
		}

	}

	private IPositionCallback callback;

	public interface IPositionCallback{
		public void getPosition(String currentCity);
		public void getPositionFail();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		if(aMapLocation == null){
			destoryLocation();

			callback.getPositionFail();
		}
	}
}
