package com.slidingmenu.fragment;

import com.sky.cookbooksa.R;
import com.sky.cookbooksa.MainActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

@SuppressLint({ "NewApi", "ValidFragment" })
public class MenuFragment extends Fragment {

	public final String tag = this.getClass().getName();

	public Context mContext = null;
	private View menuView;

	public ListView menu_lv = null;
	public MenuAdapter menuAdapter = null;


	public MenuFragment() {
		// TODO Auto-generated constructor stub
	}

	public MenuFragment(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(menuView == null){
			menuView = inflater.inflate(R.layout.menu_layout, null);

			menu_lv = (ListView) menuView.findViewById(R.id.menu_lv);
			menuAdapter = new MenuAdapter(mContext);
			menu_lv.setAdapter(menuAdapter);
			menu_lv.setOnItemClickListener(itemListener);
		}

		ViewGroup parentView = (ViewGroup)menuView.getParent();
		if(parentView != null){
			parentView.removeView(menuView);
		}

		return menuView;
	}

	// { "好友推荐", "关于我", "意见反馈", "版本更新" };
	public OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			((MainActivity)mContext).getSlidingMenu().toggle();
			Toast.makeText(mContext, menuAdapter.menuStr[position], Toast.LENGTH_SHORT).show();
			//			FragmentTransaction ft = ((MainActivityOld)mContext).getFragmentManager().beginTransaction();
			//			ContentFragment cf = new ContentFragment(menuAdapter.menuStr[position]);
			//			ft.replace(R.id.main_rl, cf);
			//			ft.commit();
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
}
