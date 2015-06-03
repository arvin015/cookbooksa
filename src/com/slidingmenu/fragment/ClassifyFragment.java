package com.slidingmenu.fragment;

import com.sky.cookbooksa.R;
import com.sky.cookbooksa.MainActivity;
import com.sky.cookbooksa.utils.Utils;
import com.slidingmenu.fragment.ClassifyNavigationHelper.INavigationCallback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

@SuppressLint("ValidFragment")
public class ClassifyFragment extends Fragment implements OnItemClickListener{

	private MainActivity act;

	private View view;//缓存View

	private GridView grid;

	private LinearLayout coverContainer;

	private int[] imgs = new int[]{R.drawable.caixi, R.drawable.zhonglei, 
			R.drawable.kouwei, R.drawable.shouyi, R.drawable.shicai };

	public ClassifyFragment(){}

	public ClassifyFragment(Context context){
		act = (MainActivity)context;
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

		if(view == null){
			view = LayoutInflater.from(act).inflate(R.layout.dish_classify, null);

			init();
		}

		ViewGroup parentView = (ViewGroup) view.getParent();
		if(parentView != null){
			parentView.removeView(view);
		}

		return view;
	}

	private void init() {
		coverContainer = (LinearLayout)view.findViewById(R.id.ll_cover);
		grid = (GridView)view.findViewById(R.id.classify_grid);
		grid.setAdapter(new ClassifyAdapter());

		setListener();
	}

	private void setListener() {
		grid.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		ClassifyNavigationHelper cnHelper = ClassifyNavigationHelper.instance(act);
		cnHelper.setNavTitle(Utils.navigations[arg2], arg2);
		cnHelper.showNavigation(coverContainer, new INavigationCallback() {

			@Override
			public void popWindowDismissed() {
				// TODO Auto-generated method stub
				coverContainer.setVisibility(View.GONE);
			}
		});

		cnHelper.cnLayout.setRadioChecked(arg2);

		coverContainer.setVisibility(View.VISIBLE);
	}

	class ClassifyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imgs.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return imgs[position];
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
				convertView = LayoutInflater.from(act).inflate(R.layout.dish_classify_item, null);

				viewHolder.rl_image_bg = (RelativeLayout)convertView.findViewById(R.id.rl_image_bg);
				viewHolder.imageView = (ImageView)convertView.findViewById(R.id.image);

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT);
				viewHolder.rl_image_bg.setLayoutParams(params);

				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.imageView.setImageResource(imgs[position]);

			return convertView;
		}

	}

	class ViewHolder{
		RelativeLayout rl_image_bg;
		ImageView imageView;
	}
}
