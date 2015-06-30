package com.sky.cookbooksa.utils;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class ExitApplication extends Application{

	private Context context;

	private List<Activity> activityList = new LinkedList<Activity>();
	private static ExitApplication instance;

	public ExitApplication(){}

	public ExitApplication(Context context){
		this.context = context;
	}

	//单例模式中获取唯一的ExitApplication实例
	public static ExitApplication getInstance(Context context){
		if(null == instance){
			instance = new ExitApplication(context);
		}
		return instance; 
	}
	//添加Activity到容器中
	public void addActivity(Activity activity){
		activityList.add(activity);
	}

	//遍历所有Activity并finish
	public void exit(){

		//退出时记录消息未读数
		SharedPreferencesUtils.getInstance(context, "").
		saveSharedPreferences(Utils.NEW_MSG_NUM, Utils.newMsgNum);

		for(Activity activity:activityList){
			activity.finish();
		}

		System.exit(0);
	}
}
