package com.slidingmenu.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import com.sky.cookbooksa.MainActivity;
import com.sky.cookbooksa.R;
import com.sky.cookbooksa.entity.ActivityInfo;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.ToastUtil;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class ImageViewPagerHelper {

	private MainActivity act;
	private View view;
	private FinalHttp fh;
	private FinalBitmap fb;
	private ViewPager viewPager;
	private ImageView lastSelectedImage;
	private LinearLayout ll_circle;

	private ArrayList<ImageView> circles;

	private ArrayList<ActivityInfo> images; 

	public ImageViewPagerHelper(MainActivity act, View view, FinalHttp fh, FinalBitmap fb){

		this.act = act;
		this.view = view;
		this.fh = fh;
		this.fb = fb;

		resetImages();
		resetCircles();

		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		viewPager = (ViewPager)view.findViewById(R.id.image_viewpager);
		ll_circle = (LinearLayout)view.findViewById(R.id.ll_circle_container);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

				circles.get(arg0).setImageResource(R.drawable.circle_selected);
				lastSelectedImage.setImageResource(R.drawable.circle_unselected);

				lastSelectedImage = circles.get(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		loadData();

	}

	private void loadData() {
		// TODO Auto-generated method stub
		fh.get(Constant.url_activityList, new AjaxCallBack<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, strMsg);
				ToastUtil.toastShort(act, "活动加载失败="+strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);

				JSONObject obj = null;
				try {
					obj = new JSONObject((String)t);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(obj != null){
					JSONArray array = null;
					try {
						array = obj.optJSONArray("result");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if(array != null){
						for(int i = 0; i < array.length(); i++){
							JSONObject object = array.optJSONObject(i);
							ActivityInfo ai = new ActivityInfo(object);
							images.add(ai);
						}
					}
				}

				viewPager.setAdapter(new ImageAdapter());

				for(int i = 0; i < images.size(); i++){
					ImageView imageView = addCircleImage();
					if(i == 0){
						imageView.setImageResource(R.drawable.circle_selected);
						lastSelectedImage = imageView;
					}

					ll_circle.addView(imageView);
				}
			}

		});
	}
	
	protected void refresh(){
		ll_circle.removeAllViews();
		resetImages();
		resetCircles();
		
		loadData();
	}

	private ImageView addCircleImage(){
		ImageView imageView = new ImageView(act);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(25, 25);
		params.leftMargin = 10;

		imageView.setLayoutParams(params);

		imageView.setImageResource(R.drawable.circle_unselected);

		circles.add(imageView);

		return imageView;
	}

	private void resetCircles(){
		if(circles == null){
			circles = new ArrayList<ImageView>();
		}
		circles.clear();
	}

	private void resetImages(){
		if(images == null){
			images = new ArrayList<ActivityInfo>();
		}
		images.clear();
	}

	class ImageAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return images.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager)container).removeView((View)object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub

			View view = LayoutInflater.from(act).inflate(R.layout.image_page_item, null);

			RelativeLayout rl_main = (RelativeLayout) view.findViewById(R.id.rl_image);
			ImageView imageView = (ImageView) view.findViewById(R.id.image);
			ProgressBar loading = (ProgressBar) view.findViewById(R.id.loading);
			loading.setVisibility(View.GONE);

			fb.display(imageView, Constant.DIR + images.get(position).getActPic());

			((ViewPager)container).addView(view);

			return view;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

	}
}