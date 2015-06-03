package com.slidingmenu.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.sky.cookbooksa.R;
import com.sky.cookbooksa.MainActivity;
import com.sky.cookbooksa.SearchResultActivity;
import com.sky.cookbooksa.adapter.RecordAdapter;
import com.sky.cookbooksa.adapter.RecordAdapter.ICallback;
import com.sky.cookbooksa.entity.SRecord;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.StringUtil;
import com.sky.cookbooksa.utils.ToastUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("ValidFragment")
public class SearchFragment extends Fragment implements OnClickListener{

	private MainActivity act;

	private View view;

	private LinearLayout ll_loading;
	private EditText search_key;
	private Button search_btn, cancel_btn;
	private TextView empty_tip;
	private ListView record_list;
	private GridView hot_grid;
	private FinalDb fd;
	private FinalHttp fh;
	private List<SRecord> records;
	private ArrayList<String> hotDishs;
	private RecordAdapter recordAdapter;
	private HotSearchAdapter hotSearchAdapter;

	private View footerview;

	public SearchFragment(){}

	public SearchFragment(Context context){
		this.act = (MainActivity)context;
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
			view = LayoutInflater.from(act).inflate(R.layout.dish_search, null);

			init();
		}

		ViewGroup parentView = (ViewGroup) view.getParent();
		if(parentView != null){
			parentView.removeView(view);
		} 

		return view;
	}

	private void init() {
		// TODO Auto-generated method stub

		fd = FinalDb.create(act);
		fh = new FinalHttp();

		ll_loading = (LinearLayout)view.findViewById(R.id.ll_loading);
		search_key = (EditText)view.findViewById(R.id.search_key);
		search_btn = (Button)view.findViewById(R.id.search_btn);
		cancel_btn = (Button)view.findViewById(R.id.cancel_btn);
		record_list = (ListView)view.findViewById(R.id.record_list);
		empty_tip = (TextView)view.findViewById(R.id.empty_tip);
		hot_grid = (GridView)view.findViewById(R.id.hot_grid);

		footerview = LayoutInflater.from(act).inflate(R.layout.footer_clearrecord, null);

		TextView clearText = (TextView) footerview.findViewById(R.id.clear_record);
		clearText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				clearAllRecord();
			}
		});

		hot_grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				searchHandle(hotDishs.get(position));
			}
		});

		loadData();//加载热门搜索、搜索记录
		setListener();
	}

	private void loadData() {
		// TODO Auto-generated method stub
		loadHotData();
		loadRecord();
	}

	private int currentPage = 1;

	private void loadHotData() {
		// TODO Auto-generated method stub
		AjaxParams params = new AjaxParams();
		params.put("page", currentPage+"");
		fh.get(Constant.url_getdishsbysearchcount, params, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, "数据加载失败="+strMsg);
				ll_loading.setVisibility(View.GONE);
			}

			@Override
			public void onSuccess(Object result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				ll_loading.setVisibility(View.GONE);
				JSONObject obj = null;
				try {
					obj = new JSONObject((String)result);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(obj != null){
					if(hotDishs == null){
						hotDishs = new ArrayList<String>();
					}
					hotDishs.clear();

					JSONArray arr  = null;
					try {
						arr = obj.optJSONArray("result");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if(arr != null){
						for(int i = 0; i < arr.length(); i++){
							hotDishs.add(arr.optJSONObject(i).optString("name"));
						}
					}

					if(hotDishs.size() > 0){
						hotSearchAdapter = new HotSearchAdapter();
						hot_grid.setAdapter(hotSearchAdapter);
					}
				}
			}

		});
	}

	private void loadRecord() {
		// TODO Auto-generated method stub
		records = fd.findAll(SRecord.class);

		recordAdapter = new RecordAdapter(act, records, fd);
		recordAdapter.setListener(new ICallback() {

			@Override
			public void deleteAllCompleted() {
				// TODO Auto-generated method stub
				empty_tip.setVisibility(View.VISIBLE);
				if(footerview != null){
					record_list.removeFooterView(footerview);
				}
			}

			@Override
			public void itemClickHandle(String key) {
				// TODO Auto-generated method stub
				searchHandle(key);
			}
		});
		record_list.setAdapter(recordAdapter);

		if(records.size() < 1){
			empty_tip.setVisibility(View.VISIBLE);
		}else{
			empty_tip.setVisibility(View.GONE);
			record_list.addFooterView(footerview);
		}
	}

	private void clearAllRecord(){
		fd.deleteByWhere(SRecord.class, null);
		records.clear();
		record_list.removeFooterView(footerview);
		notifyRecordChanged();
		empty_tip.setVisibility(View.VISIBLE);
	}

	private void setListener() {
		// TODO Auto-generated method stub
		search_btn.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
		search_key.addTextChangedListener(new TextWatcher() {//输入内容变化事件

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.length() > 1){
					cancel_btn.setVisibility(View.VISIBLE);
				}else if(s.length() < 1){
					cancel_btn.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.search_btn){
			String keyStr = search_key.getText().toString().trim();
			if(StringUtil.isEmpty(keyStr)){
				ToastUtil.toastLong(act, "搜索内容不能为空");
				return;
			}

			if(!keyIsExist(keyStr)){
				//将搜索记录保存到本地数据库--aFinal FianlDb
				SRecord sRecord = new SRecord(keyStr, String.valueOf(System.currentTimeMillis()));
				fd.save(sRecord);

				records.add(sRecord);

				notifyRecordChanged();	
			}

			if(empty_tip.getVisibility() == View.VISIBLE){
				record_list.addFooterView(footerview);
				empty_tip.setVisibility(View.GONE);
			}

			searchHandle(keyStr);
		}else if(v.getId() == R.id.cancel_btn){
			search_key.setText("");
		}
	}

	//关键字是否已存在
	private boolean keyIsExist(String key){
		for(SRecord record : records){
			if(key.equals(record.getContent())){
				return true;
			}
		}

		return false;
	}

	protected void searchHandle(String keyStr){//搜索处理
		Intent intent = new Intent(act, SearchResultActivity.class);
		intent.putExtra("searchKey",  keyStr);
		act.startActivity(intent);
		act.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void notifyRecordChanged(){
		recordAdapter.notifyDataSetChanged();
	}

	class HotSearchAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return hotDishs.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return hotDishs.get(position);
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
				convertView = LayoutInflater.from(act).inflate(R.layout.hot_grid_item, null);
				viewHolder.hot_ll_bg = (LinearLayout) convertView.findViewById(R.id.hot_ll_bg);
				viewHolder.hot_dish = (TextView) convertView.findViewById(R.id.hot_dish);

				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.hot_dish.setText(hotDishs.get(position).length() > 5?(hotDishs.get(position).substring(0, 5) 
					+ ".."):hotDishs.get(position));

			return convertView;
		}

		class ViewHolder{
			LinearLayout hot_ll_bg;
			TextView hot_dish;
		}
	}
}
