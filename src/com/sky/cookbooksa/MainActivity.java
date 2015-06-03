package com.sky.cookbooksa;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sky.cookbooksa.adapter.MyViewPagerAdapter;
import com.sky.cookbooksa.utils.DisplayUtil;
import com.sky.cookbooksa.utils.ExitApplication;
import com.sky.cookbooksa.utils.SharedPreferencesUtils;
import com.sky.cookbooksa.utils.StringUtil;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.utils.Utils;
import com.sky.cookbooksa.widget.CustomViewPager;
import com.sky.cookbooksa.widget.ViewPagerScroller;
import com.slidingmenu.fragment.ClassifyFragment;
import com.slidingmenu.fragment.MenuFragment;
import com.slidingmenu.fragment.RecommendFragment;
import com.slidingmenu.fragment.RecommendFragment.STYLE;
import com.slidingmenu.fragment.SearchFragment;
import com.slidingmenu.fragment.UserInfoFragment;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity implements OnPageChangeListener{

	protected Context context;

	protected CustomViewPager pager;
	private ImageButton menu, setting;
	private ToggleButton listStyle;
	private PagerAdapter mAdapter;
	private float screenWidth;//屏幕宽度
	private SlidingMenu sm;
	private TextView title;

	private FragmentTransaction ft;

	private RecommendFragment rfragment;

	private long lastTime = 0;

	private long totalTime = 2000;

	private ArrayList<Fragment> fragments;
	private ArrayList<RadioButton> titles = new ArrayList<RadioButton>();//五个标题
	private String[] titleNames = new String[]{
			"推荐", "分类", "搜索", "个人中心"
	};

	private SharedPreferencesUtils spfu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ExitApplication.getInstance().addActivity(this);
		DisplayUtil.getInstance(this);

		context = this;

		//		setContentView(R.layout.activity_main);///slidingmenu里面重写了
		initSlidingMenu();//初始化右边滑屏，这里面有setContentView函数，必须最前面
		initView();//初始化控件
		initTitle();
		initViewPager();
	}
	/**
	 * 初始化视图
	 */
	private void initView() {
		// TODO Auto-generated method stub

		spfu = SharedPreferencesUtils.getInstance(this, "");

		Utils.isLoaded = !StringUtil.isEmpty(
				spfu.loadStringSharedPreference(Utils.USER_PHONE));
		Utils.userPhone = spfu.loadStringSharedPreference(Utils.USER_PHONE);
		Utils.userId = spfu.loadStringSharedPreference(Utils.USER_ID);
		Utils.userNick = spfu.loadStringSharedPreference(Utils.USER_NICK);
		Utils.userPic = spfu.loadStringSharedPreference(Utils.USER_PIC);

		pager = (CustomViewPager) findViewById(R.id.viewpager);//初始化控件
		pager.setPagingEnabled(false);//设置是否可翻页
		menu = (ImageButton) findViewById(R.id.menu);
		menu.setVisibility(View.VISIBLE);
		setting = (ImageButton) findViewById(R.id.setting);
		title = (TextView) findViewById(R.id.title);
		listStyle = (ToggleButton) findViewById(R.id.list_style);
		listStyle.setVisibility(View.VISIBLE);
		title.setText(titleNames[0]);

		menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toggle();
			}
		});

		listStyle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			//ToggleButton切换
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){//grid版式
					rfragment.changeStyle(STYLE.GRID);
				}else{//list版式
					rfragment.changeStyle(STYLE.LIST);
				}
			}
		});

		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, SettingActivity.class);
				context.startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});

		fragments = new ArrayList<Fragment>();//初始化数据
		//		fragments.add(new HomeFragment(this));
		rfragment = new RecommendFragment(this);
		fragments.add(rfragment);
		fragments.add(new ClassifyFragment(this));
		fragments.add(new SearchFragment(this));
		fragments.add(new UserInfoFragment(this));
	}
	/**
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		// TODO Auto-generated method stub
		mAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), fragments);
		pager.setAdapter(mAdapter);
		//		pager.setOnPageChangeListener(this);
		pager.setCurrentItem(0);//设置成当前第一个

		//设置ViewPager切换滚动速度
		ViewPagerScroller vps = new ViewPagerScroller(context);
		vps.initViewPagerScroll(pager);
		vps.setScrollDuration(0);
	}
	/**
	 * 初始化几个用来显示title的RadioButton
	 */
	private void initTitle() {
		//		titles.add((RadioButton) findViewById(R.id.home));//三个title标签
		titles.add((RadioButton) findViewById(R.id.dish));
		titles.add((RadioButton) findViewById(R.id.classify));
		titles.add((RadioButton) findViewById(R.id.search));
		titles.add((RadioButton) findViewById(R.id.userinfo));

		for(int i = 0; i < titles.size(); i++){//设置响应事件
			titles.get(i).setOnClickListener(new MyOnClickListener(i));
		}
	}
	/**
	 * 初始化开源组件SlidingMenu
	 */
	@SuppressLint("NewApi")
	private void initSlidingMenu() {
		// 实例化滑动菜单对象  
		sm = getSlidingMenu(); 
		setContentView(R.layout.activity_main);//设置当前的视图
		setBehindContentView(R.layout.menu_layout);//设置左页视图

		//将左侧Menu交给menuFragment处理
		MenuFragment menuFragment = new MenuFragment(this);
		ft = this.getFragmentManager().beginTransaction();
		ft.replace(R.id.menu_fl, menuFragment);
		ft.commit();

		sm.setMode(SlidingMenu.LEFT);
		// 设置滑动阴影的宽度  
		//		sm.setShadowWidthRes(R.dimen.shadow_width);  
		// 设置滑动阴影的图像资源  
		//		sm.setShadowDrawable(R.drawable.shadow);  
		// 设置滑动菜单视图的宽度  
		//		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);  
		// 设置渐入渐出效果的值  
		sm.setFadeDegree(0.35f);  
		// 设置触摸屏幕的模式  
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN); 
		//		sm.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);//设置左页的响应范围
		//		sm.setTouchmodeMarginThreshold(60);//这个设置的是隔屏幕边缘多远开始响应
		//		sm.setBehindOffset(50);//设置左页距离屏幕右边缘的距离，右页会补上

		WindowManager wManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wManager.getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;//获取屏幕的宽度
		sm.setBehindWidth((int) (screenWidth*0.8));//设置左页的宽度
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	/**
	 * 重写OnClickListener的响应函数，主要目的就是实现点击title时，pager会跟着响应切换
	 */
	private class MyOnClickListener implements OnClickListener{
		private int index;

		public MyOnClickListener(int index){
			this.index=index;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			pager.setCurrentItem(index);//把viewpager的视图切过去，实现捏造title跟pager的联动
			titles.get(index).setChecked(true);//设置被选中，否则布局里面的背景不会切换
			title.setText(titleNames[index]);

			if(index == 0){
				setListStyleBtnVisibility(View.VISIBLE);
			}else{
				setListStyleBtnVisibility(View.GONE);
			}

			if(index == 3){
				setting.setVisibility(View.VISIBLE);
			}else{
				setting.setVisibility(View.GONE);
			}
		}

	}

	public void setListStyleBtnVisibility(int visible){
		listStyle.setVisibility(visible);
	}

	public void setCurrentPager(int item){
		pager.setCurrentItem(item);
	}

	public void setCurrentCheckedTitle(int item){
		titles.get(item).setChecked(true);
	}

	//exit app
	private void exitApp(){
		ExitApplication.getInstance().exit();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			if(System.currentTimeMillis() - lastTime > totalTime){
				lastTime = System.currentTimeMillis();
				ToastUtil.toastShort(this, "再按一次退出饭吧！");
			}else{
				exitApp();
			}
		}
		return true;
	}

	/**
	 * 下面三个是OnPageChangeListener的接口函数
	 */
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	@Override
	public void onPageSelected(int arg0) {
		Log.i("slide","onPageSelected+agr0="+arg0);
		title.setText(titleNames[arg0]);
		titles.get(arg0).setChecked(true);//保持页面跟按钮的联动

		if(arg0 == 1){
			listStyle.setVisibility(View.VISIBLE);
		}else{
			listStyle.setVisibility(View.GONE);
		}

		if(arg0 == 0){
			// 如果当前是第一页，那么设置触摸屏幕的模式为全屏模式  
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//设置成全屏响应
		}else {
			// 如果不是第一页，设置触摸屏幕的模式为边缘60px的地方  
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		}
	}
}