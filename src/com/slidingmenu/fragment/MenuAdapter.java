package com.slidingmenu.fragment;

import com.sky.cookbooksa.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {

	private Context context;

	public String[] menuStr = { "好友推荐", "关于我", "意见反馈", "版本更新" };
	public int[] viewId = { R.drawable.recommendfriend, R.drawable.about,
			R.drawable.suggestion, R.drawable.refurbish };
	public LayoutInflater inflater = null;

	public MenuAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return menuStr.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return menuStr[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	class Holder {
		ImageView items_iv;
		TextView items_tv;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.menu_items, null);
			holder = new Holder();
			holder.items_iv = (ImageView) convertView
					.findViewById(R.id.items_iv);
			holder.items_tv = (TextView) convertView
					.findViewById(R.id.items_tv);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.items_iv.setBackgroundResource(viewId[position]);
		holder.items_tv.setText(menuStr[position]);
		return convertView;
	}
	//释放
	public void exit() {
		menuStr = null;
		inflater = null;
	}
}
