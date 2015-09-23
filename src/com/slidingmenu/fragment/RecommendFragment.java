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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.pulltorefresh.lib.ILoadingLayout;
import com.pulltorefresh.lib.MyOnBorderListener;
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
import com.sky.cookbooksa.widget.ScrollViewExtend;
import com.sky.cookbooksa.widget.WaterfallView;

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
    private ScrollViewExtend scrollView;

    private ILoadingLayout loadLayoutTop;//顶部刷新View
    private View footerView;//底部加载更多View

    private ImageView goTopImg;
    private List<Dish> dishs;
    private MainActivity act;
    private FinalHttp fh;
    private FinalBitmap fb;
    private int page = 1;
    private int show_item = 10;
    private int count = 100;
    private int currentCount = 0;
    private boolean isFirst = true;
    private TextView empty_tip, search_key;

    private ImageViewPagerHelper ivpHelper;

    private String lastRefreshTime;

    private SharedPreferencesUtils spfu;

    private IRecommendFragmentCallback listener;

    private boolean isLoading = false;

    private WaterfallView waterfallView;

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

        mScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
        search_key = (TextView) view.findViewById(R.id.search_key);
        waterfallView = (WaterfallView) view.findViewById(R.id.waterfallView);
        empty_tip = (TextView) view.findViewById(R.id.empty_tip);
        goTopImg = (ImageView) view.findViewById(R.id.goTopImg);

        waterfallView.init(2);

        goTopImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (scrollView != null) {
                    scrollView.setScrollY(0);
                }

                goTopImg.setVisibility(View.GONE);
            }
        });

        initPullRefreshScrollView();

        ivpHelper = new ImageViewPagerHelper(act, view, fh, fb);

        loadData(false);

    }

    private void initPullRefreshScrollView() {
        // TODO Auto-generated method stub
        mScrollView.setMode(Mode.PULL_FROM_START);

        scrollView = mScrollView.getRefreshableView();

        loadLayoutTop = mScrollView.getLoadingLayoutProxy(true, false);
        loadLayoutTop.setPullLabel("下拉刷新...");
        loadLayoutTop.setRefreshingLabel("正在刷新...");
        loadLayoutTop.setReleaseLabel("松开刷新...");
        loadLayoutTop.setLoadingDrawable(act.getResources().getDrawable(R.drawable.default_ptr_rotate));

        mScrollView.setScrollingWhileRefreshingEnabled(true);//刷新时是否可滑动
        mScrollView.setPullToRefreshOverScrollEnabled(false);//设置滑动是否引起刷新

        //设置滑动停止事件
        mScrollView.setOnScrollListener(new ScrollViewExtend.IScrollViewListener() {
            @Override
            public void scrollStop() {

                if (!mScrollView.isRefreshing()) {
                    setTopImgVisibility();
                }
            }
        });

        //设置滑动到达边界事件
        mScrollView.setOnBorderListener(new MyOnBorderListener() {

            @Override
            public void onScroll() {

                goTopImg.setVisibility(View.GONE);
            }

            @Override
            public void onScrollToBottom() {

                currentCount = waterfallView.getAllChildNum();

                if (count == currentCount) {

                    if (isFirst) {
                        Toast.makeText(act, "数据加载完毕", Toast.LENGTH_SHORT).show();
                        isFirst = false;
                    }

                } else {

                    if (!isLoading) {

                        if (footerView == null) {
                            footerView = LayoutInflater.from(act).inflate(R.layout.loadingmore, null);
                        }

                        if (scrollView.getChildAt(0) != null) {
                            ((ViewGroup) (scrollView.getChildAt(0))).addView(footerView);
                        }

                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });

                        loadData(false);

                        goTopImg.setVisibility(View.GONE);

                        isLoading = true;

                    }
                }

            }
        });

        //设置拉动事件
        mScrollView.setOnPullEventListener(new OnPullEventListener<ScrollViewExtend>() {

            @Override
            public void onPullEvent(PullToRefreshBase<ScrollViewExtend> refreshView,
                                    State state, Mode direction) {
                // TODO Auto-generated method stub

                //位于顶部下拉设置上次更新时间
                if (state == State.PULL_TO_REFRESH &&
                        direction == Mode.PULL_FROM_START) {
                    setLastRefreshTime();
                }

            }
        });

        //设置刷新事件
        mScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollViewExtend>() {

            /**
             * 该刷新了回调方法
             * @param refreshView
             */
            @Override
            public void onRefresh(PullToRefreshBase<ScrollViewExtend> refreshView) {
                resetDishs();

                page = 1;

                loadData(true);
                ivpHelper.refresh();
            }
        });

    }

    /**
     * 设置向上图片显示隐藏
     */
    private void setTopImgVisibility() {
        if (scrollView != null) {

            if (scrollView.getScrollY() >= scrollView.getHeight() * 2) {

                goTopImg.setVisibility(View.VISIBLE);
            } else {
                goTopImg.setVisibility(View.GONE);
            }
        }

    }

    private void setListener() {
        search_key.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                act.setCurrentPager(2);
                act.setCurrentCheckedTitle(2);

                listener.searchClick();
            }
        });
    }

    private void loadData(final boolean isTopRefresh) {
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

                if (isTopRefresh) {
                    mScrollView.onRefreshComplete();
                } else {
                    isLoading = false;
                    if (scrollView.getChildAt(0) != null) {
                        ((ViewGroup) (scrollView.getChildAt(0))).removeView(footerView);
                    }
                }

                setTopImgVisibility();
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

                    if (isTopRefresh) {//刷新，清空原有布局View
                        waterfallView.resetCurrentColumn();
                        waterfallView.removeAllChild();

                        isFirst = true;

                        mScrollView.onRefreshComplete();
                    } else {//加载更多
                        isLoading = false;
                        if (scrollView.getChildAt(0) != null) {
                            ((ViewGroup) (scrollView.getChildAt(0))).removeView(footerView);
                        }
                    }

                    JSONArray jsonArray = obj.optJSONArray("result");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Dish dish = new Dish(object.toString());
                            dishs.add(dish);

                            View childView = setView(dish);

                            waterfallView.addItemToLayout(childView);
                        }
                    }

                    page++;

                    setTopImgVisibility();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * 创建菜肴View
     *
     * @param dish
     * @return
     */
    private View setView(final Dish dish) {

        View view = null;

        view = LayoutInflater.from(act).inflate(R.layout.dish_grid_item, null);
        FrameLayout rl_main = (FrameLayout) view.findViewById(R.id.rl_main);

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
        return view;
    }

    private void goDetail(String currentId) {
        Intent intent = new Intent();
        intent.putExtra("id", currentId);
        intent.setClass(act, DishDetailActivity.class);

        act.startActivity(intent);
        act.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
