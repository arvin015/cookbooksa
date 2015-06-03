package com.sky.cookbooksa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import com.sky.cookbooksa.entity.Region;
import com.sky.cookbooksa.entity.RegionDao;
import com.sky.cookbooksa.utils.AssetsDatabaseManager;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.PositionManager;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.utils.Utils;
import com.sky.cookbooksa.utils.PositionManager.IPositionCallback;
import com.sky.cookbooksa.widget.QuickAlphabeticBar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 
 * @author arvin.li
 *
 */
public class ChoiceCityActivity extends BaseActivity {

	private LinearLayout positionContainer;
	private ImageButton backBtn;
	private ListView listView;
	private QuickAlphabeticBar bar;
	private TextView titleText, positionText;

	private ArrayList<Region> regionList;

	private RegionAdapter regionAdapter;

	private Region currentRegion;

	private int parentId = 1;

	private String province, city;

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 001){

				setAdapter();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.region_main);

		init();
	}

	private void init() {
		// TODO Auto-generated method stub

		AssetsDatabaseManager.initManager(this);

		initView();

		getPosition();
	}

	private void initView() {
		// TODO Auto-generated method stub

		positionContainer = (LinearLayout) findViewById(R.id.positionContainer);
		listView = (ListView) findViewById(R.id.listView);
		bar = (QuickAlphabeticBar) findViewById(R.id.quickBar);
		backBtn = (ImageButton) findViewById(R.id.back);
		titleText = (TextView) findViewById(R.id.title);
		positionText = (TextView) findViewById(R.id.positionText);

		backBtn.setVisibility(View.VISIBLE);
		titleText.setText("选择城市");

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if(flag == 1){
					back();
				}else if(flag == 2){

					parentId = 1;

					flag = 1;
				}else if(flag == 3){

					parentId = currentRegion.getParentId();

					flag = 2;
				}

				loadData(parentId);
			}
		});

		loadData(parentId);
	}

	//定位
	private void getPosition() {
		// TODO Auto-generated method stub
		PositionManager.getInstance(context).requestPosition(new IPositionCallback() {

			@Override
			public void getPositionFail() {
				// TODO Auto-generated method stub
				positionText.setText("定位失败");
			}

			@Override
			public void getPosition(String currentCity) {
				// TODO Auto-generated method stub
				positionText.setText(currentCity);

				positionContainer.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						submitHandle(positionText.getText().toString());
					}
				});
			}
		});
	}

	private int flag = 1;//1表示省，2表示市，3表示县

	private void setAdapter() {
		// TODO Auto-generated method stub
		regionAdapter = new RegionAdapter();
		listView.setAdapter(regionAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				if(flag != 3){
					currentRegion = regionList.get(arg2);
				}

				String cityName = regionList.get(arg2).getRegionName();
				int regionId = regionList.get(arg2).getRegionId();

				if(flag == 1){

					loadData(regionId);

					flag = 2;

					province = cityName + " ";

				}else if(flag == 2){

					loadData(regionId);

					flag = 3;

					city = cityName + " ";

				}else if(flag == 3){

					submitHandle(new StringBuffer().append(province).append(city).append(cityName).toString());

				}

			}
		});

		bar.init(ChoiceCityActivity.this);
		bar.setListView(listView);
		bar.setHight(bar.getHeight());
		bar.setVisibility(View.VISIBLE);
	};

	private void loadData(final int parentId) {
		// TODO Auto-generated method stub

		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				RegionDao regionDao = new RegionDao();
				regionList = regionDao.quaryRegionListByParentId(parentId);
				handler.sendEmptyMessage(001);
			}

		}).start();

	}

	private void submitHandle(final String address) {
		// TODO Auto-generated method stub
		AjaxParams params = new AjaxParams();
		params.put("key", "address");
		params.put("content", address);
		params.put("phone", Utils.userPhone);

		fh.post(Constant.url_userupdate, params, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, strMsg);
				ToastUtil.toastShort(context, "修改失败="+strMsg);
			}

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);

				JSONObject obj = null;
				String result = null;
				try {
					obj = new JSONObject((String)t);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(obj != null){
					result = obj.optString("state");
				}

				if("true".equals(result)){
					ToastUtil.toastShort(context, "地址修改成功！");

					Intent intent = new Intent();
					intent.putExtra("city", address);

					setResult(RESULT_OK, intent);

					back();
				}else{
					ToastUtil.toastShort(context, "地址修改失败！");
				}
			}
		});
	}

	class RegionAdapter extends BaseAdapter{

		private LayoutInflater inflater;

		private HashMap<String, Integer> alphaIndexer;

		public RegionAdapter(){

			inflater = LayoutInflater.from(context);

			alphaIndexer = new HashMap<String, Integer>();

			for(int i = 0; i < regionList.size(); i++){

				String alpha = getAlpha(regionList.get(i).getRegionEnName());

				if(!alphaIndexer.containsKey(alpha)){
					alphaIndexer.put(alpha, i);
				}
			}

			bar.setAlphaIndexer(alphaIndexer);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return regionList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return regionList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub

			ViewHolder viewHolder = null;

			if(arg1 == null){

				viewHolder = new ViewHolder();

				arg1 = inflater.inflate(R.layout.region_item, null);

				viewHolder.alpha = (TextView) arg1.findViewById(R.id.alpha);
				viewHolder.regionName = (TextView) arg1.findViewById(R.id.regionName);

				arg1.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) arg1.getTag();
			}

			String currentAlpha = getAlpha(regionList.get(arg0).getRegionEnName());

			if(arg0 == 0){

				viewHolder.alpha.setVisibility(View.VISIBLE);

			}else{

				String lastAlpha = getAlpha(regionList.get(arg0 - 1).getRegionEnName());

				if(("" + currentAlpha).equals(lastAlpha)){
					viewHolder.alpha.setVisibility(View.GONE);
				}else{
					viewHolder.alpha.setVisibility(View.VISIBLE);
				}
			}

			viewHolder.regionName.setText(regionList.get(arg0).getRegionName());
			viewHolder.alpha.setText(currentAlpha);

			return arg1;
		}

		class ViewHolder{
			TextView alpha, regionName;
		}

	}

	/**
	 * 获取首字母
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式匹配
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(); // 将小写字母转换为大写
		} else {
			return "#";
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		PositionManager.getInstance(context).destoryLocation();
	}

}
