package com.sky.cookbooksa.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 
 * @author arvin.li
 *
 */
public class CustomViewPager extends ViewPager{

	private boolean enabled = true;

	public CustomViewPager(Context context){
		super(context);
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {//拦截Touch事件
		// TODO Auto-generated method stub
		if(enabled){
			return super.onInterceptTouchEvent(arg0);
		}else{
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if(enabled){
			return super.onTouchEvent(arg0);
		}else{
			return false;
		}

	}

	/**
	 * ViewPager是否可滑动翻页 
	 * @param enabled
	 */
	public void setPagingEnabled(boolean enabled){
		this.enabled = enabled;
	}

}
