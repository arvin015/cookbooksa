package com.sky.cookbooksa;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.sky.cookbooksa.adapter.CollectAdapter;
import com.sky.cookbooksa.entity.Collect;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.utils.Utils;
import com.sky.cookbooksa.widget.CommonGridView;
import com.sky.cookbooksa.widget.CustomViewPager;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class CollectActivity extends BaseActivity{

	private TextView title;
	private ImageButton backBtn;
	private RadioGroup radioGroup;
	private CustomViewPager pager;
	private View dishLine, personLine;

	public enum AJAX_MODE{
		DISH, PERSON
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collect_main);

		init();
	}

	private void init(){

		title = (TextView) findViewById(R.id.title);
		title.setVisibility(View.VISIBLE);
		backBtn = (ImageButton) findViewById(R.id.back);
		backBtn.setVisibility(View.VISIBLE);
		radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
		pager = (CustomViewPager) findViewById(R.id.viewpager);
		dishLine = (View) findViewById(R.id.line_dish);
		personLine = (View) findViewById(R.id.line_person);

		title.setText("我的收藏");

		pager.setAdapter(new MyAdapter());

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				back();
			}
		});

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == R.id.radio_dish){
					pager.setCurrentItem(0);
					dishLine.setVisibility(View.VISIBLE);
					personLine.setVisibility(View.INVISIBLE);
				}else{
					pager.setCurrentItem(1);
					dishLine.setVisibility(View.INVISIBLE);
					personLine.setVisibility(View.VISIBLE);
				}
			}
		});

		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if(arg0 == 0){
					dishLine.setVisibility(View.VISIBLE);
					personLine.setVisibility(View.INVISIBLE);
				}else{
					dishLine.setVisibility(View.INVISIBLE);
					personLine.setVisibility(View.VISIBLE);
				}
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

	class MyAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager)container).removeViewAt(position);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub

			View view = LayoutInflater.from(context).inflate(R.layout.collect, null);

			CommonGridView gridView = (CommonGridView) view.findViewById(R.id.gridview);
			TextView emptyTip = (TextView) view.findViewById(R.id.empty_tip);

			String url;
			AJAX_MODE mode;

			if(position == 0){
				url = Constant.url_getalllovedishbyuserid;
				mode = AJAX_MODE.DISH;
			}else{
				url = Constant.url_getalllovepersonbyuserid;
				mode = AJAX_MODE.PERSON;
			}

			AjaxParams params = new AjaxParams();
			params.put("userId", Utils.userId);

			fh.post(url, params, new MyAjaxCallback(mode, gridView, emptyTip));

			((ViewPager)container).addView(view);

			return view;
		}

		class MyAjaxCallback extends AjaxCallBack<Object>{

			private CommonGridView gridView;
			private TextView emptyTip;

			private AJAX_MODE mode;

			private List<Collect> list;

			public MyAjaxCallback(AJAX_MODE mode, CommonGridView gridView, TextView emptyTip){
				this.mode = mode;
				this.gridView = gridView;
				this.emptyTip = emptyTip;
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, strMsg);

				ToastUtil.toastShort(context, "加载数据失败="+strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);

				JSONObject json = null;

				try {
					json = new JSONObject((String)t);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(json != null){

					int count = json.optInt("count");

					if(count == 0){

						if(mode == AJAX_MODE.DISH){
							emptyTip.setVisibility(View.VISIBLE);
							emptyTip.setText("还没有收藏菜肴哦！");
						}else{
							emptyTip.setVisibility(View.VISIBLE);
							emptyTip.setText("还没有关注的人哦！");
						}
					}else{
						JSONArray arr = null;
						try {
							arr = json.optJSONArray("result");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if(arr != null){

							list = new ArrayList<Collect>();

							for(int i = 0; i < arr.length(); i++){
								list.add(new Collect(arr.optJSONObject(i)));
							}

							CollectAdapter adapter = new CollectAdapter(context, list, fb, mode);
							gridView.setAdapter(adapter);
						}

					}
				}
			}
		}

	}
}
