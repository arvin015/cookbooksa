package com.slidingmenu.fragment;

import com.sky.cookbooksa.MainActivity;
import com.sky.cookbooksa.R;
import com.sky.cookbooksa.SearchResultActivity;
import com.sky.cookbooksa.utils.PopupWindowUtil;
import com.sky.cookbooksa.utils.PopupWindowUtil.IPopupWindowCallback;
import com.sky.cookbooksa.utils.Utils;
import com.sky.cookbooksa.widget.ClassifyNavigationRadioGroup;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class ClassifyNavigationHelper {

	private View showView;

	private MainActivity act;

	protected ClassifyNavigationRadioGroup cnLayout;
	private ListView listView;
	protected LinearLayout content_container;
	private ImageView backImage;
	private TextView navTitle;

	private static ClassifyNavigationHelper cnHelper;

	private int index;

	private String title;

	private int screenWidth, screenHeight;

	private ClassifyAdapter adapter;

	private final String[] TASTE = new String[]{
			"家常味", "特辣", "中辣", "微辣", "清淡"	
	};

	private final String[] STYLE = new String[]{
			"家常菜", "湘菜", "川菜", "粤菜", "鲁菜", "浙菜", "苏菜", "闽菜", 
			"徽菜", "西北菜", "东北菜", "台湾菜", "日韩料理"
	};

	private final String[] TYPE = new String[]{
			"素食", "海鲜", "汤", "粥", "小吃", "面食"
	};

	private final String[] METHOD = new String[]{
			"爆炒", "清蒸", "水煮", "油煎", "油炸", "清炒", "焖", "烘焙"	
	};

	private final String[] INGREDIENTS = new String[]{
			"龙虾", "豆腐", "鲜肉", "腊肉", "鸡蛋", "鲤鱼", "芹菜", "香干", "腐竹", "排骨"
	};

	private final String[][] classifies = new String[][]{
			STYLE, TYPE, TASTE, METHOD, INGREDIENTS
	};

	private final int[] icons = new int[]{
			R.drawable.type_icon, R.drawable.style_icon, R.drawable.taste_icon,
			R.drawable.method_icon, R.drawable.ingredient_icon
	};

	public ClassifyNavigationHelper(Context act){
		this.act = (MainActivity)act;

		WindowManager wm = (WindowManager)act.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	public static ClassifyNavigationHelper instance(Context act){
		if(cnHelper == null){
			cnHelper = new ClassifyNavigationHelper(act);
		}

		return cnHelper;
	}

	public void setNavTitle(String title, int index){
		this.title = title;
		this.index = index;
	}

	public void showNavigation(View parent, final INavigationCallback callback){
		if(showView == null){

			showView = LayoutInflater.from(act).inflate(R.layout.dish_classify_show, null);

			cnLayout = (ClassifyNavigationRadioGroup) showView.findViewById(R.id.ll_navigation);
			listView = (ListView) showView.findViewById(R.id.classify_listview);
			content_container = (LinearLayout) showView.findViewById(R.id.content_container);
			backImage = (ImageView) showView.findViewById(R.id.goback);
			navTitle = (TextView) showView.findViewById(R.id.navigation_title);

			backImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					PopupWindowUtil.getInstance().dismiss();
				}
			});

			cnLayout.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub
					RadioButton currentRadio = (RadioButton) group.findViewById(checkedId);
					navTitle.setText(Utils.navigations[(Integer) currentRadio.getTag()]);

					index = (Integer) currentRadio.getTag();

					objects = classifies[(Integer) currentRadio.getTag()];

					if(adapter != null){
						adapter.notifyDataSetChanged();
					}
				}
			});

			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(act, SearchResultActivity.class);
					intent.putExtra("classifyKey", classifies[index][position]);
					act.startActivity(intent);
					act.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			});
		}

		objects = classifies[index];
		adapter = new ClassifyAdapter();

		listView.setAdapter(adapter);

		navTitle.setText(title);

		PopupWindowUtil.getInstance().setPopupWindowSize(screenWidth * 9 / 10, screenHeight * 3 / 4);
		PopupWindowUtil.getInstance().setPopuWindow(showView, R.style.classify_popup_animation, new IPopupWindowCallback() {

			@Override
			public void popWindowDismissed() {
				// TODO Auto-generated method stub
				callback.popWindowDismissed();
			}
		});
		PopupWindowUtil.getInstance().showAtLocation(parent, Gravity.LEFT, 0, 0);
	}

	private String[] objects;

	class ClassifyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return objects.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return objects[position];
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

				convertView = LayoutInflater.from(act).inflate(R.layout.classify_show_item, null);
				viewHolder.container = (LinearLayout) convertView.findViewById(R.id.classify_item_container);
				viewHolder.imageView = (ImageView) convertView.findViewById(R.id.classify_image);
				viewHolder.content = (TextView) convertView.findViewById(R.id.classify_content);

				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.content.setText(objects[position]);
			viewHolder.imageView.setBackgroundResource(icons[index]);

			return convertView;
		}

		class ViewHolder{
			LinearLayout container;
			ImageView imageView;
			TextView content;
		}

	}

	public interface INavigationCallback{
		public void popWindowDismissed();
	}
}
