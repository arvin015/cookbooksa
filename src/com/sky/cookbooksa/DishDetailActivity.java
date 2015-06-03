package com.sky.cookbooksa;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.sky.cookbooksa.entity.Dish;
import com.sky.cookbooksa.entity.PictureInfo;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.ShowDialog;
import com.sky.cookbooksa.utils.StringUtil;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.utils.Utils;
import com.sky.cookbooksa.widget.CommonListView;
import com.sky.cookbooksa.widget.RoundImageView;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class DishDetailActivity extends BaseActivity implements OnClickListener, OnItemClickListener{

	private TextView title, dish_style, dish_during, dish_taste;
	private TextView dish_name, dish_desc, enter_step_detail, dish_tip, user_nick;
	private CommonListView step_list, ing_list, sea_list;
	private ImageButton back;
	private ImageView userpic, dishpic;
	private ImageButton gohome, gocomment, gocollect, goshare;
	private String dishId;
	private ProgressDialog dialog;
	private Dish dish;
	private GoodsAdapter goodsAdapter;
	private StepAdapter stepAdapter;
	private ArrayList<PictureInfo> pics;
	private String userId = "0";
	private String isCollect = "1"; //1未收藏，0已收藏

	private OnekeyShare oks;

	protected enum AJAX_MODE{
		ISCOLLECT, LOADDATA, COLLECTDISH
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dish_detail);

		dishId = getIntent().getExtras().getString("id");

		init();
	}

	private void init() {

		ShareSDK.initSDK(context);
		oks = new OnekeyShare();

		title = (TextView)findViewById(R.id.title);
		title.setText("菜谱详情");
		back = (ImageButton)findViewById(R.id.back);
		back.setVisibility(View.VISIBLE);
		dish_name = (TextView)findViewById(R.id.dish_name);
		dish_desc = (TextView)findViewById(R.id.dish_desc);
		dish_during = (TextView)findViewById(R.id.dish_during);
		dish_taste = (TextView)findViewById(R.id.dish_taste);
		dish_style = (TextView)findViewById(R.id.dish_style);
		dish_tip = (TextView)findViewById(R.id.dish_tip);
		enter_step_detail = (TextView)findViewById(R.id.enter_step_detail);
		ing_list = (CommonListView)findViewById(R.id.ing_list);
		sea_list = (CommonListView)findViewById(R.id.sea_list);
		step_list = (CommonListView)findViewById(R.id.step_list);
		userpic = (ImageView)findViewById(R.id.userpic);
		user_nick = (TextView)findViewById(R.id.user_nick);
		gocollect = (ImageButton)findViewById(R.id.gocollect);
		gohome = (ImageButton)findViewById(R.id.gohome);
		gocomment = (ImageButton)findViewById(R.id.gocomment);
		goshare = (ImageButton)findViewById(R.id.goshare);
		dishpic = (ImageView)findViewById(R.id.dishpic);

		setListener();
		loadData();
		checkIsCollect();
	}

	private void checkIsCollect() {//当前登录用户是否已收藏该菜肴
		AjaxParams params = new AjaxParams();
		params.put("userId", userId);
		params.put("dishId", dishId);
		fh.get(Constant.url_iscollect, params, new MyAjaxCallBack(AJAX_MODE.ISCOLLECT));
	}

	private void setListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				back();
			}
		});
		
		userpic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		gohome.setOnClickListener(this);
		goshare.setOnClickListener(this);
		gocollect.setOnClickListener(this);
		gocomment.setOnClickListener(this);
		enter_step_detail.setOnClickListener(this);
		step_list.setOnItemClickListener(this);
	}

	private void loadData(){
		dialog = ShowDialog.showDialog(context, "数据加载中...");
		AjaxParams params = new AjaxParams();
		params.put("id", dishId);

		if(!StringUtil.isEmpty(Utils.userId)){//登录用户将此次查看作为一次足迹
			params.put("userId", Utils.userId);
		}

		fh.get(Constant.url_dishdetail, params, new MyAjaxCallBack(AJAX_MODE.LOADDATA));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.enter_step_detail:
			enterLarge(0);
			break;
		case R.id.gohome:
			goHome();
			break;
		case R.id.goshare:
			goShare();
			break;
		case R.id.gocomment:
			goComment();
			break;
		case R.id.gocollect:
			goCollect();
			break;
		}
	}

	private void goHome() {
		intentHandle(MainActivity.class, null, true);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	private void goComment() {
		Bundle bundle = new Bundle();
		bundle.putString("dishId", dishId);
		intentHandle(CommentActivity.class, bundle, false);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	private void goShare() {
		oks.disableSSOWhenAuthorize();

		oks.setTitle(dish.getName());

		oks.setText(dish.getDesc());

		oks.setImageUrl(Constant.DIR + dish.getMainPic());

		oks.show(context);
	}

	private void goCollect() {
		AjaxParams params = new AjaxParams();
		params.put("userId", userId+"");
		params.put("dishId", dishId+"");
		if("0".equals(isCollect)){
			params.put("handle", "delete");
		}else{
			params.put("handle", "add");
		}
		fh.get(Constant.url_dishcollect, params, new MyAjaxCallBack(AJAX_MODE.COLLECTDISH));
	}

	//步骤Item点击事件
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		enterLarge(arg2);
	}

	private void enterLarge(int position) {//大图浏览

		resetPics();

		for(int i = 0; i < dish.getSteps().length; i++){
			PictureInfo info = new PictureInfo();
			info.setDesc(dish.getSteps()[i]);
			info.setPath(dish.getStep_imgs()[i]);
			pics.add(info);
		}

		Bundle bundle = new Bundle();
		bundle.putSerializable("list", pics);
		bundle.putInt("currentPosition", position);

		intentHandle(ImagePagerActivity.class, bundle, false);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	private void resetPics(){
		if(pics == null){
			pics = new ArrayList<PictureInfo>();
		}

		pics.clear();
	}

	protected void back() {
		super.back();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			back();
		}
		return true;
	}

	class MyAjaxCallBack extends AjaxCallBack<Object>{

		private AJAX_MODE mode;

		public MyAjaxCallBack(AJAX_MODE mode){
			this.mode = mode;
		}

		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			// TODO Auto-generated method stub
			super.onFailure(t, errorNo, strMsg);

			if(mode == AJAX_MODE.LOADDATA){
				if(dialog != null && dialog.isShowing()){
					dialog.dismiss();
				}
			}

		}

		@Override
		public void onSuccess(Object t) {
			// TODO Auto-generated method stub
			super.onSuccess(t);

			JSONObject object = null;
			try {
				object = new JSONObject((String)t);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if(mode == AJAX_MODE.ISCOLLECT){

				isCollect = object.optString("state");
				if("0".equals(isCollect)){
					gocollect.setImageResource(R.drawable.btn_cancel_favorite_normal);
				}else if("1".equals(isCollect)){
					gocollect.setImageResource(R.drawable.btn_favorite_normal);
				}else{
					ToastUtil.toastLong(context, "数据加载失败");
				}
			}else if(mode == AJAX_MODE.LOADDATA){
				dialog.dismiss();
				JSONArray jsonArray = null;
				try {
					jsonArray = object.optJSONArray("result");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return;
				}

				if(jsonArray == null){
					return;
				}

				JSONObject obj = jsonArray.optJSONObject(0);

				if(obj == null){
					return;
				}

				dish = new Dish(obj);

				//赋值
				dish_name.setText(dish.getName());
				dish_desc.setText(dish.getDesc());
				dish_style.setText(dish.getStyle());
				dish_tip.setText(dish.getTip());
				dish_taste.setText(dish.getTaste());
				dish_during.setText(dish.getDuring());
				user_nick.setText(dish.getUserInfo().getuNick());
				//				String imgStr = dish.getUserInfo().getuPhoto();
				//				imgStr = imgStr.substring(imgStr.lastIndexOf("/")+1);
				//
				//				fb.configLoadfailImage(R.drawable.dish_detail_bg);
				//				fb.display(dishpic, Constant.DIR + imgStr);

				dishpic.setImageResource(R.drawable.dish_detail_bg);

				fb.configLoadfailImage(R.drawable.image1);
				fb.display(userpic, Constant.DIR + dish.getUserInfo().getuPhoto());

				goodsAdapter = new GoodsAdapter(dish.getIngredients(), dish.getIngQuantity());
				ing_list.setAdapter(goodsAdapter);

				goodsAdapter = new GoodsAdapter(dish.getSeasoning(), dish.getSeaQuantity());
				sea_list.setAdapter(goodsAdapter);

				stepAdapter = new StepAdapter();
				step_list.setAdapter(stepAdapter);
			}else{

				String result = object.optString("state");
				if("true".equals(result)){
					if("0".equals(isCollect)){
						ToastUtil.toastLong(context, "取消收藏成功");
						gocollect.setImageResource(R.drawable.btn_favorite_normal);
						isCollect = "1";
					}else{
						ToastUtil.toastLong(context, "收藏成功");
						gocollect.setImageResource(R.drawable.btn_cancel_favorite_normal);
						isCollect = "0";
					}
				}else{
					if("0".equals(isCollect)){
						ToastUtil.toastLong(context, "取消收藏失败");
					}else{
						ToastUtil.toastLong(context, "收藏失败");
					}
				}
			}
		}
	}

	class GoodsAdapter extends BaseAdapter{

		private String[] goods;
		private String[] qua;

		public GoodsAdapter(String[] goods, String[] qua){
			this.goods = goods;
			this.qua = qua;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return goods.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return goods[position];
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

				convertView = LayoutInflater.from(context).inflate(R.layout.goods_item, null);

				viewHolder.goodsName = (TextView)convertView.findViewById(R.id.goods_name);
				viewHolder.goodsQua = (TextView)convertView.findViewById(R.id.goods_qua);

				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.goodsName.setText(goods[position]);
			viewHolder.goodsQua.setText(qua[position]);

			return convertView;
		}

		class ViewHolder{
			TextView goodsName;
			TextView goodsQua;
		}

	}

	class StepAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dish.getSteps().length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return dish.getSteps()[position];
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

				convertView = LayoutInflater.from(context).inflate(R.layout.step_item, null);

				viewHolder.stepNo = (TextView)convertView.findViewById(R.id.step_no);
				viewHolder.stepDesc = (TextView)convertView.findViewById(R.id.step_desc);
				viewHolder.mainPic = (RoundImageView)convertView.findViewById(R.id.mainpic);

				viewHolder.stepNo.setText((position+1)+"");
				viewHolder.stepDesc.setText(dish.getSteps()[position]);

				String imgStr = dish.getStep_imgs()[position];
				imgStr = imgStr.substring(imgStr.lastIndexOf("/")+1);
				fb.display(viewHolder.mainPic, Constant.DIR+imgStr);

				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}

			return convertView;
		}

		class ViewHolder{
			TextView stepNo;
			TextView stepDesc;
			RoundImageView mainPic;
		}

	}

}
