package com.sky.cookbooksa.adapter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;

import com.sky.cookbooksa.R;
import com.sky.cookbooksa.entity.Dish;
import com.sky.cookbooksa.utils.Constant;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DishGridAdapter extends BaseAdapter{

	private Context context;
	private List<Dish> dishs;
	private FinalBitmap fb;
	private int screenWidth;

	public DishGridAdapter(Context context, List<Dish> dishs){
		this.context = context;
		this.dishs = dishs;
		fb = FinalBitmap.create(context);
		fb.configLoadfailImage(R.drawable.photo_loading);
		fb.configLoadingImage(R.drawable.photo_loading);
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dishs.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return dishs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder viewHolder = null;

		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.dish_grid_item, null);
			viewHolder.rl_image = (RelativeLayout)convertView.findViewById(R.id.rl_image);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(screenWidth/2-10,
					LayoutParams.WRAP_CONTENT);
			viewHolder.rl_image.setLayoutParams(params);

			viewHolder.imageView = (ImageView)convertView.findViewById(R.id.mainpic);
			viewHolder.name = (TextView)convertView.findViewById(R.id.name);
			viewHolder.during = (TextView)convertView.findViewById(R.id.during);

			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.name.setText(dishs.get(position).getName());
		viewHolder.during.setText(dishs.get(position).getDuring());
		fb.display(viewHolder.imageView, Constant.DIR + dishs.get(position).getMainPic());

		return convertView;
	}

	class ViewHolder{
		RelativeLayout rl_image;
		ImageView imageView;
		TextView name;
		TextView during;
	}

}
