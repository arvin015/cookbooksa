package com.sky.cookbooksa;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class WelcomeActivity extends BaseActivity{

	private ViewPager viewPager;
	private LinearLayout circleContainer;

	private int[] images = new int[]{
			R.drawable.wel1, R.drawable.wel2, R.drawable.wel3, R.drawable.wel4
	};

	private ArrayList<ImageView> circleList;

	private ImageView currentCircle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.welcome);

		init();
	}

	private void init(){

		circleList = new ArrayList<ImageView>();

		circleContainer = (LinearLayout) findViewById(R.id.circleContainer);
		viewPager = (ViewPager) findViewById(R.id.welcomePager);

		initCircle();

		viewPager.setAdapter(new WelcomeAdapter());

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				currentCircle.setImageResource(R.drawable.circle_unselected);
				circleList.get(arg0).setImageResource(R.drawable.circle_selected);

				currentCircle = circleList.get(arg0);
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
	}

	private void initCircle() {
		// TODO Auto-generated method stub
		for(int i = 0; i < images.length; i++){
			ImageView imageView = createCircleImage(i);
			circleContainer.addView(imageView);

			circleList.add(imageView);
		}
	}

	private ImageView createCircleImage(int index){
		ImageView imageView = new ImageView(context);

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		int leftMargin = 15;
		if(index == 0){
			leftMargin = 0;
		}
		params.leftMargin = leftMargin;
		imageView.setLayoutParams(params);

		if(index == 0){
			imageView.setImageResource(R.drawable.circle_selected);
			currentCircle = imageView;
		}else{
			imageView.setImageResource(R.drawable.circle_unselected);
		}

		return imageView;
	}

	class WelcomeAdapter extends PagerAdapter{

		private ArrayList<View> viewList;
		private LayoutInflater inflater;

		public WelcomeAdapter(){
			viewList = new ArrayList<View>();

			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return images.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager)container).removeView(viewList.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub

			View view = inflater.inflate(R.layout.welcome_item, null);

			FrameLayout welcomeBg = (FrameLayout) view.findViewById(R.id.welcomeBg);

			welcomeBg.setBackgroundResource(images[position]);

			if(position == images.length - 1){
				welcomeBg.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						intentHandle(MainActivity.class, null, true);
					}
				});
			}

			viewList.add(view);

			((ViewPager)container).addView(view);

			return view;
		}

	}
}
