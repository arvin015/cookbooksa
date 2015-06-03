package com.sky.cookbooksa.adapter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;

import com.sky.cookbooksa.CollectActivity.AJAX_MODE;
import com.sky.cookbooksa.R;
import com.sky.cookbooksa.entity.Collect;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.DisplayUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CollectAdapter extends BaseAdapter{

	private Context context;
	private List<Collect> list;

	private FinalBitmap fb;

	private int screenWidth;

	private LayoutInflater inflater;

	private AJAX_MODE mode;

	public CollectAdapter(Context context, List<Collect> list, 
			FinalBitmap fb, AJAX_MODE mode){
		this.context = context;
		this.list = list;
		this.fb = fb;
		this.mode = mode;

		screenWidth = DisplayUtil.screenWidth;

		inflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder viewHolder;

		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.collect_item, null);

			viewHolder.rlImg = (RelativeLayout) convertView.findViewById(R.id.rl_img);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageview);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.textview);

			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				(int)((screenWidth - 60) / 3), (int)((screenWidth - 60) / 3));
		viewHolder.rlImg.setLayoutParams(params);

		String path = list.get(position).getMainPic();

		if(mode == AJAX_MODE.DISH){
			viewHolder.textView.setText(list.get(position).getDishName());

			path = path.substring(path.lastIndexOf("/") + 1);
		}else{
			viewHolder.textView.setText(list.get(position).getUserNick());
		}

		fb.display(viewHolder.imageView, Constant.DIR + path,
				(int)((screenWidth - 60) / 3), (int)((screenWidth - 60) / 3));

		return convertView;
	}

	class ViewHolder{
		RelativeLayout rlImg;
		ImageView imageView;
		TextView textView;
	}

}
