package com.sky.cookbooksa;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.sky.cookbooksa.entity.Dish;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.StringUtil;
import com.sky.cookbooksa.utils.ToastUtil;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SearchResultActivity extends BaseActivity{

	private Context context;
	private ListView listView;
	private TextView title, emptyTip;
	private ImageButton backBtn;
	private String searchKey, classifyKey;
	private int currentPage = 1;

	private int resultCount = 0;
	private List<Dish> dishs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);

		searchKey = getIntent().getStringExtra("searchKey");//获取搜索关键字

		classifyKey = getIntent().getStringExtra("classifyKey");//获取分类类型名

		init();
	}

	private void init() {
		// TODO Auto-generated method stub

		context = this;

		dishs = new ArrayList<Dish>();

		loading(getString(R.string.loading_msg));

		listView = (ListView) findViewById(R.id.searchlist);
		title = (TextView) findViewById(R.id.title);
		emptyTip = (TextView) findViewById(R.id.search_empty_tip);
		backBtn = (ImageButton) findViewById(R.id.back);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				back();
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putString("id", dishs.get(position).getId());
				intentHandle(DishDetailActivity.class, bundle, false);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});

		loadData();

	}

	private void loadData() {
		// TODO Auto-generated method stub
		AjaxParams params = new AjaxParams();
		params.put("key", StringUtil.isEmpty(searchKey)?classifyKey:searchKey);
		params.put("page", currentPage+"");

		String urlStr = StringUtil.isEmpty(searchKey)?Constant.url_getdishsbyclassify:Constant.url_dishsearch;

		fh.get(urlStr, params, new AjaxCallBack<Object>() {
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, strMsg);

				loadMissed();

				if(StringUtil.isEmpty(searchKey)){
					title.setText(Html.fromHtml("<font>"+classifyKey+"（共 <span style=\"font-size:24px;\">"+resultCount+"</span> 条数据）</font>"));
				}else{
					title.setText(Html.fromHtml("<font>搜索到 <span style=\"font-size:24px;\">"+resultCount+"</span> 条结果</font>"));
				}
				ToastUtil.toastShort(context, "加载数据失败="+strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);

				loadMissed();

				JSONObject obj = null;
				try {
					obj = new JSONObject((String)t);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(obj != null){
					resultCount = obj.optInt("count");

					JSONArray array = null;
					try {
						array = obj.optJSONArray("result");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if(array != null){
						for(int i = 0; i < array.length(); i++){
							Dish dish = new Dish(array.optJSONObject(i));
							if(dish != null){
								dishs.add(dish);
							}
						}
					}
				}

				if(dishs.size() < 1){
					emptyTip.setVisibility(View.VISIBLE);
				}else{
					listView.setAdapter(new SearchAdapter(dishs));
				}

				if(StringUtil.isEmpty(searchKey)){
					title.setText(Html.fromHtml("<font>"+classifyKey+"（共 <span style=\"font-size:24px;\">"+resultCount+"</span> 条数据）</font>"));
				}else{
					title.setText(Html.fromHtml("<font>搜索到 <span style=\"font-size:24px;\">"+resultCount+"</span> 条结果</font>"));
				}
			}
		});
	}

	class SearchAdapter extends BaseAdapter{

		private List<Dish> dishs;

		public SearchAdapter(List<Dish> dishs){
			this.dishs = dishs;
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

			Dish dish = dishs.get(position);

			ViewHolder viewHolder;

			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.dish_item, null);

				viewHolder.ll_line = (LinearLayout)convertView.findViewById(R.id.ll_cut);
				viewHolder.ll_line.setVisibility(View.GONE);
				viewHolder.ll_container = (LinearLayout)convertView.findViewById(R.id.ll_main);
				viewHolder.ll_container.setBackgroundResource(R.drawable.griditem_bg);
				viewHolder.imageView = (ImageView)convertView.findViewById(R.id.mainpic);
				viewHolder.name = (TextView)convertView.findViewById(R.id.name);
				viewHolder.during = (TextView)convertView.findViewById(R.id.during);
				viewHolder.style = (TextView)convertView.findViewById(R.id.style);
				viewHolder.desc = (TextView)convertView.findViewById(R.id.desc);

				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.name.setText(Html.fromHtml(StringUtil.isEmpty(searchKey)?dish.getName()
					:replaceString(searchKey, dish.getName())));

			viewHolder.style.setText(dish.getStyle());

			String desc = dish.getDesc().length() > 30?dish.getDesc().substring(0, 30)+"...":dish.getDesc();
			viewHolder.desc.setText(Html.fromHtml(StringUtil.isEmpty(searchKey)?desc:replaceString(searchKey, desc)));

			viewHolder.during.setText(dish.getDuring());

			fb.display(viewHolder.imageView, Constant.DIR + dish.getMainPic()
					.substring(dish.getMainPic().lastIndexOf("/") + 1));

			return convertView;
		}

		class ViewHolder{
			LinearLayout ll_container;
			LinearLayout ll_line;
			ImageView imageView;
			TextView name;
			TextView during;
			TextView style;
			TextView desc;
		}

	}

	private String replaceString(String key, String str){
		return str.replace(key, "<font color=\"#FF0000\">"+key+"</font>");
	}
}
