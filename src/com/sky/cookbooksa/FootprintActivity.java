package com.sky.cookbooksa;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.sky.cookbooksa.entity.Footprint;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.PopupWindowUtil;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.utils.Utils;
import com.sky.cookbooksa.widget.CommonScrollView;
import com.sky.cookbooksa.widget.CommonScrollView.OnBorderListener;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FootprintActivity extends BaseActivity{

	private CommonScrollView scrollView;
	private LinearLayout footContainer;
	private TextView title, emptyTip;
	private ImageButton backBtn;

	private ArrayList<Footprint> foots;

	private int count;//总共记录数

	private int page = 1;

	private Typeface tf;

	private View childView;

	private View choicedView;

	private int tag = 1;//View tagId

	private enum AJAX_MODE{
		GET, DELETE
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.footprint);

		init();
	}

	private void init(){

		loading("数据加载中...");

		title = (TextView) findViewById(R.id.title);
		title.setVisibility(View.VISIBLE);
		backBtn = (ImageButton) findViewById(R.id.back);
		backBtn.setVisibility(View.VISIBLE);
		emptyTip = (TextView) findViewById(R.id.empty_tip);

		scrollView = (CommonScrollView) findViewById(R.id.foot_scrollview);
		footContainer = (LinearLayout) findViewById(R.id.foot_container);

		title.setText("我的足迹");
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				back();
			}
		});

		scrollView.setOnBorderListener(new OnBorderListener() {

			@Override
			public void scroll() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTop() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onBottom() {
				// TODO Auto-generated method stub

			}
		});

		loadData();
	}

	private void loadData() {
		// TODO Auto-generated method stub
		AjaxParams params = new AjaxParams();
		params.put("userId", Utils.userId);
		params.put("page", page + "");

		fh.post(Constant.url_getallfootbyuserid, params, new MyAjaxCallback(AJAX_MODE.GET));
	}

	private View createLineView(Footprint footprint){

		View view = LayoutInflater.from(context).inflate(R.layout.line, null);

		view.setTag(tag);

		tag++;

		return view;
	}

	private View createContentView(final Footprint footprint){
		final View view = LayoutInflater.from(context).inflate(R.layout.footprint_item, null);

		TextView content = (TextView) view.findViewById(R.id.foot_content);
		TextView time = (TextView) view.findViewById(R.id.foot_time);

		if(tf == null){
			AssetManager am = getAssets();
			tf = Typeface.createFromAsset(am, "font/fzst.ttf");
		}
		content.setTypeface(tf);

		content.setText(Html.fromHtml("你浏览了<font color=\"#FF8000\">【"+footprint.getDishName()+"】菜肴</font>"));
		time.setText(footprint.getFootTime());

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putString("id", footprint.getDishId());
				intentHandle(DishDetailActivity.class, bundle, false);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});

		view.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub

				choicedView = view;

				if(childView == null){
					childView = LayoutInflater.from(context).inflate(R.layout.pup_delete, null);

					childView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							PopupWindowUtil.getInstance().dismiss();

							AjaxParams params = new AjaxParams();
							params.put("footId", footprint.getFootId() + "");

							fh.post(Constant.url_deletefootbyfootid, params, 
									new MyAjaxCallback(AJAX_MODE.DELETE));
						}
					});
				}

				PopupWindowUtil.getInstance().setPopuWindow(childView, -1, null);

				PopupWindowUtil.getInstance().showAsDropDown(v, view.getMeasuredWidth() / 2 - 20, 
						-view.getMeasuredHeight() - 85);

				return true;
			}
		});

		view.setTag(tag);

		tag++;

		return view;
	}

	private int getCurentViewPosition(){//获取当前删除的View在Parent View中所在的位置

		int result = -1;

		for(int i = 0; i < footContainer.getChildCount(); i++){
			View v = footContainer.getChildAt(i);
			if(v.getTag() == choicedView.getTag()){
				result = i;
				break;
			}
		}

		return result;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		loadMissed();
	}

	class MyAjaxCallback extends AjaxCallBack<Object>{

		private AJAX_MODE mode;

		public MyAjaxCallback(AJAX_MODE mode){
			this.mode = mode;
		}

		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			// TODO Auto-generated method stub
			super.onFailure(t, errorNo, strMsg);

			loadMissed();

			if(mode == AJAX_MODE.GET){
				ToastUtil.toastShort(context, "加载足迹失败="+strMsg);
			}else if(mode == AJAX_MODE.DELETE){
				ToastUtil.toastShort(context, "删除足迹失败="+strMsg);
			}
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
				if(mode == AJAX_MODE.GET){

					loadMissed();

					count = json.optInt("count");

					title.setText("足迹(共"+count+"条)");

					if(count < 1){
						emptyTip.setVisibility(View.VISIBLE);
					}

					JSONArray arr = null;
					try {
						arr = json.optJSONArray("result");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if(arr != null){
						foots = new ArrayList<Footprint>();

						for(int i = 0; i < arr.length(); i++){
							foots.add(new Footprint(arr.optJSONObject(i)));
						}

						for(int j = 0; j < foots.size(); j++){

							View lineView = createLineView(foots.get(j));
							View contentView = createContentView(foots.get(j));

							footContainer.addView(lineView);
							footContainer.addView(contentView);
						}
					}
				}else{
					if(mode == AJAX_MODE.DELETE){
						String state = json.optString("state");
						if("true".equals(state)){
							ToastUtil.toastShort(context, "删除成功");

							count--;

							title.setText("足迹(共"+count+"条)");

							int index = getCurentViewPosition();

							if(index != -1 && index != 0){//获取当前删除的足迹View上一个View并删除之
								footContainer.removeView(footContainer.getChildAt(index - 1));
							}

							footContainer.removeView(choicedView);//删除当前足迹View

						}else{
							ToastUtil.toastShort(context, "删除失败");
						}
					}
				}
			}

		}
	}

}
