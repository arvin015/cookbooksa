package com.slidingmenu.fragment;

import com.sky.cookbooksa.R;
import com.sky.cookbooksa.MainActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("ValidFragment")
public class HomeFragment extends Fragment{

	private MainActivity act;

	private View view;//用来缓存之前的页面，防止它重复Inflate页面

	public HomeFragment(){}

	public HomeFragment(Context context){
		act = (MainActivity)context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if(act == null)
			return;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (view == null) {
			view = inflater.inflate(R.layout.home, container, false);
		}
		// 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生IllegalStateException。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view; 
		//下面这种没有缓存页面，每次都会重新加载数据，数据多会闪烁
		//		View view=inflater.inflate(R.layout.job_fragment,container, false);
		//		return view;
	}

	@Override
	public void onPause(){
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
}
