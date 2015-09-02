package com.slidingmenu.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.pulltorefresh.lib.ILoadingLayout;
import com.pulltorefresh.lib.PullToRefreshBase;
import com.pulltorefresh.lib.PullToRefreshBase.Mode;
import com.pulltorefresh.lib.PullToRefreshBase.OnPullEventListener;
import com.pulltorefresh.lib.PullToRefreshBase.State;
import com.pulltorefresh.lib.PullToRefreshScrollView;
import com.sky.cookbooksa.DishDetailActivity;
import com.sky.cookbooksa.IRecommendFragmentCallback;
import com.sky.cookbooksa.MainActivity;
import com.sky.cookbooksa.R;
import com.sky.cookbooksa.entity.Dish;
import com.sky.cookbooksa.uihelper.ImageViewPagerHelper;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.SharedPreferencesUtils;
import com.sky.cookbooksa.utils.StringUtil;
import com.sky.cookbooksa.utils.ToastUtil;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class RecommendFragment extends Fragment {

    private View view;//缓存页面

    private PullToRefreshScrollView mScrollView;

    private ILoadingLayout loadLayoutTop, loadLayoutBottom;

    private List<View> views, newviews;
    private List<Dish> dishs;
    private MainActivity act;
    private FinalHttp fh;
    private FinalBitmap fb;
    private int page = 1;
    private int show_item = 10;
    private int count = 100;
    private int currentCount = 0;
    private LinearLayout ll_left, ll_right;
    private boolean isFirst = true;
    private TextView empty_tip, search_key;

    private ImageViewPagerHelper ivpHelper;

    private String lastRefreshTime;

    private SharedPreferencesUtils spfu;

    private IRecommendFragmentCallback listener;

    private STYLE style = STYLE.GRID;

    public enum STYLE {
        LIST, GRID
    }

    public RecommendFragment() {
    }

    public RecommendFragment(MainActivity act) {
        this.act = act;

        this.listener = act;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (act == null)
            return null;

        if (view == null) {
            view = inflater.inflate(R.layout.dish, container, false);

            init();
            setListener();
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);//先移除
        }

        return view;
    }

    private void init() {
        fh = new FinalHttp();

        fb = FinalBitmap.create(act);
        fb.configLoadfailImage(R.drawable.photo_loading);
        fb.configLoadingImage(R.drawable.photo_loading);

        spfu = SharedPreferencesUtils.getInstance(act, null);//单例模式

        resetDishs();
        resetViews();

        mScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
        search_key = (TextView) view.findViewById(R.id.search_key);
        ll_left = (LinearLayout) view.findViewById(R.id.ll_left);
        ll_right = (LinearLayout) view.findViewById(R.id.ll_right);
        empty_tip = (TextView) view.findViewById(R.id.empty_tip);

        initPullRefreshScrollView();

        ivpHelper = new ImageViewPagerHelper(act, view, fh, fb);

        loadData(false);
    }

    private void initPullRefreshScrollView() {
        // TODO Auto-generated method stub
        mScrollView.setMode(Mode.BOTH);//上下都可刷新

        loadLayoutTop = mScrollView.getLoadingLayoutProxy(true, false);
        loadLayoutBottom = mScrollView.getLoadingLayoutProxy(false, true);

        loadLayoutTop.setPullLabel("下拉刷新...");
        loadLayoutTop.setRefreshingLabel("正在刷新...");
        loadLayoutTop.setReleaseLabel("松开刷新...");

        loadLayoutBottom.setPullLabel("加载更多数据...");
        loadLayoutBottom.setRefreshingLabel("加载更多数据...");
        loadLayoutBottom.setReleaseLabel("加载更多数据...");

        loadLayoutTop.setLoadingDrawable(act.getResources().getDrawable(R.drawable.default_ptr_rotate));
        loadLayoutBottom.setLoadingDrawable(null);

        mScrollView.setScrollingWhileRefreshingEnabled(true);
//        mScrollView.setPullToRefreshOverScrollEnabled(false);//设置滑动是否引起刷新

        mScrollView.setOnPullEventListener(new OnPullEventListener<ScrollView>() {

            @Override
            public void onPullEvent(PullToRefreshBase<ScrollView> refreshView,
                                    State state, Mode direction) {
                // TODO Auto-generated method stub

                Log.d("print", "onPullEvent====" + state + "   direction=" + direction);

                //位于顶部下拉设置上次更新时间
                if (state == State.PULL_TO_REFRESH &&
                        direction == Mode.PULL_FROM_START) {
                    setLastRefreshTime();
                }

                //滑动到底部立马执行加载更多操作
                if (state == State.OVERSCROLLING &&
                        direction == Mode.PULL_FROM_END) {

                    mScrollView.setRefreshing();
                }
            }
        });

        mScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            /**
             * 该刷新了回调方法
             * @param refreshView
             */
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                // TODO Auto-generated method stub

                resetDishs();
                resetViews();

                page = 1;

                loadData(true);
                ivpHelper.refresh();

                mScrollView.setMode(Mode.BOTH);//重新设置上下都可刷新

            }

            /**
             * 该加载更多了回调方法
             * @param refreshView
             */
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                // TODO Auto-generated method stub

                if (style == STYLE.GRID) {
                    currentCount = ll_left.getChildCount() + ll_right.getChildCount();
                } else {
                    currentCount = ll_right.getChildCount();
                }
                if (count == currentCount) {
//                    if (isFirst) {
                    Toast.makeText(act, "数据加载完毕", Toast.LENGTH_SHORT).show();
//                        isFirst = false;
//                    }

                    mScrollView.onRefreshComplete();//停止刷新

                    mScrollView.setMode(Mode.PULL_FROM_START);//移除底部加载更多View

                } else {
                    loadData(false);
                }
            }
        });
    }

    private void setListener() {
        search_key.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                act.setCurrentPager(2);
                act.setCurrentCheckedTitle(2);
                act.setListStyleBtnVisibility(View.GONE);

                listener.searchClick();
            }
        });
    }

    private void loadData(final boolean isRefresh) {
        String url = null;
        AjaxParams params = new AjaxParams();
        url = Constant.url_dishlist;
        params.put("page_count", show_item + "");
        params.put("page", page + "");
        fh.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.toastLong(act, "数据加载失败=" + strMsg);

                mScrollView.onRefreshComplete();
            }

            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);
                try {
                    JSONObject obj = new JSONObject(result.toString());
                    count = obj.optInt("count");
                    if (count < 1) {
                        empty_tip.setVisibility(View.VISIBLE);
                        return;
                    }

                    resetNewViews();

                    JSONArray jsonArray = obj.optJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Dish dish = new Dish(object.toString());
                        dishs.add(dish);
                        newviews.add(setView(dish));
                    }

                    if (isRefresh) {//刷新，清空原有布局View
                        ll_left.removeAllViews();
                        ll_right.removeAllViews();
                    }

                    addView(newviews);
                    views.addAll(newviews);
                    page++;

                    mScrollView.onRefreshComplete();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });
    }

    private void addView(List<View> views) {

        for (int j = 0; j < views.size(); j++) {
            if (style == STYLE.GRID) {
                if (j % 2 == 0) {
                    ll_left.addView(views.get(j));
                } else {
                    ll_right.addView(views.get(j));
                }
            } else {
                ll_right.addView(views.get(j));
            }
        }
    }

    private View setView(final Dish dish) {
        View view = null;
        if (style == STYLE.LIST) {//list布局
            view = LayoutInflater.from(act).inflate(R.layout.dish_item, null);
            LinearLayout ll_main = (LinearLayout) view.findViewById(R.id.ll_main);

            ImageView imageView = (ImageView) view.findViewById(R.id.mainpic);
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView during = (TextView) view.findViewById(R.id.during);
            TextView style = (TextView) view.findViewById(R.id.style);
            TextView desc = (TextView) view.findViewById(R.id.desc);

            String nameStr = dish.getName();
            String duringStr = dish.getDuring();
            String descStr = dish.getDesc();
            if (descStr.length() > 20) {
                descStr = descStr.substring(0, 20) + "...";
            }
            String styleStr = dish.getStyle();
            String mainpicStr = dish.getMainPic();
            mainpicStr = mainpicStr.substring(mainpicStr.lastIndexOf("/") + 1);
            name.setText(nameStr);
            desc.setText("简介:" + descStr);
            style.setText(styleStr);
            during.setText("历时:" + duringStr);
            fb.display(imageView, Constant.DIR + mainpicStr);

            ll_main.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    goDetail(dish.getId());
                }
            });
        } else {//grid布局
            view = LayoutInflater.from(act).inflate(R.layout.dish_grid_item, null);
            RelativeLayout rl_main = (RelativeLayout) view.findViewById(R.id.rl_main);

            ImageView imageView = (ImageView) view.findViewById(R.id.mainpic);
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView style = (TextView) view.findViewById(R.id.style);
            TextView during = (TextView) view.findViewById(R.id.during);

            name.setText(dish.getName());
            style.setText(dish.getStyle());
            during.setText(dish.getDuring());
            String mainpicStr = dish.getMainPic();
            mainpicStr = mainpicStr.substring(mainpicStr.lastIndexOf("/") + 1);
            fb.display(imageView, Constant.DIR + mainpicStr);

            rl_main.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    goDetail(dish.getId());
                }
            });
        }
        return view;
    }

    private void goDetail(String currentId) {
        Intent intent = new Intent();
        intent.putExtra("id", currentId);
        intent.setClass(act, DishDetailActivity.class);

        act.startActivity(intent);
        act.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    //改变显示布局
    public void changeStyle(STYLE type) {
        // TODO Auto-generated method stub

        ll_left.removeAllViews();
        ll_right.removeAllViews();

        style = type;

        resetViews();

        for (int i = 0; i < dishs.size(); i++) {
            if (type == STYLE.GRID) {//grid
                ll_left.setVisibility(View.VISIBLE);
            } else {//list
                ll_left.setVisibility(View.GONE);
            }
            views.add(setView(dishs.get(i)));
        }
        addView(views);
    }

    private void setLastRefreshTime() {
        // TODO Auto-generated method stub
        lastRefreshTime = spfu.loadStringSharedPreference("lastRefreshTime");

        if (lastRefreshTime == null) {
            loadLayoutTop.setLastUpdatedLabel("第一次刷新");
        } else {
            loadLayoutTop.setLastUpdatedLabel(StringUtil.friendly_time(lastRefreshTime) + "刷新");
        }

        spfu.saveSharedPreferences("lastRefreshTime", StringUtil.gettimenow());
    }

    private void resetDishs() {
        if (dishs == null) {
            dishs = new ArrayList<Dish>();
        }
        dishs.clear();
    }

    private void resetViews() {
        if (views == null) {
            views = new ArrayList<View>();
        }
        views.clear();
    }

    private void resetNewViews() {
        if (newviews == null) {
            newviews = new ArrayList<View>();
        }
        newviews.clear();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

        }
        return false;
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    interface ICallback {
        public void changeStyle(int type);
    }
}
