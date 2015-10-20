package com.slidingmenu.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sky.cookbooksa.DishDetailActivity;
import com.sky.cookbooksa.IRecommendFragmentCallback;
import com.sky.cookbooksa.MainActivity;
import com.sky.cookbooksa.R;
import com.sky.cookbooksa.entity.Dish;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.SharedPreferencesUtils;
import com.sky.cookbooksa.utils.StringUtil;
import com.sky.cookbooksa.widget.ScaleImageView;
import com.waterfull.lib.PLA_AbsListView;
import com.waterfull.lib.PLA_AdapterView;
import com.waterfull.view.XListViewAd;
import com.waterfull.view.XMultiListView;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

@SuppressLint("ValidFragment")
public class RecommendFragment_old extends Fragment implements XMultiListView.IXListViewListener {

    private View view;//缓存页面

    private MainActivity act;
    private FinalHttp fh;
    private FinalBitmap fb;
    private int page = 1;
    private int show_item = 10;
    private int count = 100;
    private int currentCount = 0;
    private boolean isFirst = true;

    private LinearLayout loadingContainer;
    private ImageView loadingImg;
    private TextView tipText;

    private AnimationDrawable animation;

    private String lastRefreshTime;

    private SharedPreferencesUtils spfu;

    private IRecommendFragmentCallback listener;

    private XMultiListView waterfallView;

    private XListViewAd xListViewAd;

    private LinkedList<Dish> dishList;

    private DishAdapter dishAdapter;

    public RecommendFragment_old() {
    }

    public RecommendFragment_old(MainActivity act) {
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
            view = inflater.inflate(R.layout.dish_old, container, false);

            init();
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);//先移除
        }

        return view;
    }

    private void init() {

        dishList = new LinkedList<>();

        fh = new FinalHttp();

        fb = FinalBitmap.create(act);
        fb.configLoadfailImage(R.drawable.photo_loading);
        fb.configLoadingImage(R.drawable.photo_loading);

        spfu = SharedPreferencesUtils.getInstance(act, null);//单例模式

        waterfallView = (XMultiListView) view.findViewById(R.id.multiListView);
        loadingContainer = (LinearLayout) view.findViewById(R.id.loadingConainer);
        tipText = (TextView) view.findViewById(R.id.tipText);
        loadingImg = (ImageView) view.findViewById(R.id.loadingImg);

        initLoadingAnim();

        waterfallView.setPullRefreshEnable(true);
        waterfallView.setPullLoadEnable(true);
        waterfallView.setXListViewListener(this);

        xListViewAd = new XListViewAd(act);
        waterfallView.addMoreHeaderView(xListViewAd);

        waterfallView.getmFooterView().hide();//进来时先隐藏加载更多Loading

        waterfallView.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
//                if (position > waterfallView.getHeaderViewsCount() - 1) {//除去header's view
//                    goDetail(dishList.get(position - waterfallView.getHeaderViewsCount()).getId());
//                }
            }
        });

        waterfallView.setOnScrollListener(new XMultiListView.OnXScrollListener() {
            @Override
            public void onXScrolling(View view) {
            }

            @Override
            public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {

                //滑动停止
                if (scrollState == SCROLL_STATE_IDLE) {
                }
            }

            @Override
            public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    if (!waterfallView.ismPullLoading()) {
                        waterfallView.startLoadMore();
                    }
                }
            }
        });

        dishAdapter = new DishAdapter(act);
        waterfallView.setAdapter(dishAdapter);

        loadData(false);
    }

    /**
     * 初始化加载动画
     */
    private void initLoadingAnim() {
        animation = (AnimationDrawable) loadingImg.getBackground();
        if (animation != null)
            animation.start();

        tipText.setText("加载中...");
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

                //加载失败提示
                if (animation != null && animation.isRunning()) {
                    animation.stop();
                    loadingImg.setBackgroundResource(R.drawable.empty_error);
                    tipText.setText("加载失败");
                }

                if (isTopRefresh) {
                    waterfallView.stopRefresh();
                } else {
                    waterfallView.stopLoadMore();
                }

            }

            @Override
            public void onSuccess(Object result) {
                super.onSuccess(result);

                if (animation != null && animation.isRunning()) {
                    animation.stop();
                }

                try {
                    JSONObject obj = new JSONObject(result.toString());
                    count = obj.optInt("count");
                    if (count < 1) {
                        loadingImg.setBackgroundResource(R.drawable.empty_no_data);
                        tipText.setText("数据为空");
                        return;
                    }

                    //隐藏加载动画
                    loadingContainer.setVisibility(View.GONE);

                    if (isTopRefresh) {//刷新，清空原有布局View

                        dishList.clear();

                        isFirst = true;

                        waterfallView.setPullLoadEnable(true);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        waterfallView.stopRefresh();
                                    }
                                });
                            }
                        }, 1000);

                    } else {//加载更多

                        waterfallView.getmFooterView().show();//显示加载更多Loading

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        waterfallView.stopLoadMore();
                                    }
                                });
                            }
                        }, 1000);
                    }

                    ArrayList<Dish> list = new ArrayList<>();

                    JSONArray jsonArray = obj.optJSONArray("result");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            list.add(new Dish(object.toString()));
                        }
                    }

                    dishList.addAll(list);
                    dishAdapter.notifyDataSetChanged();

                    page++;

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * 刷新操作
     */
    @Override
    public void onRefresh() {
        page = 1;

        loadData(true);

        xListViewAd.refresh();//刷新广告
    }

    /**
     * 加载更多操作
     */
    @Override
    public void onLoadMore() {

        currentCount = waterfallView.getmTotalItemCount() -
                waterfallView.getHeaderViewsCount() - waterfallView.getFooterViewsCount();

        if (count == currentCount) {

            if (isFirst) {
                Toast.makeText(act, "数据加载完毕", Toast.LENGTH_SHORT).show();
                isFirst = false;

                waterfallView.setPullLoadEnable(false);
            }

        } else {

            loadData(false);

        }
    }

    /**
     * 设置上次刷新时间
     */
    @Override
    public void onSetRefreshTime() {
        setLastRefreshTime();
    }

    class DishAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public DishAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return dishList.size();
        }

        @Override
        public Object getItem(int i) {
            return dishList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            final Dish dish = dishList.get(i);

            ViewHolder viewHolder;

            if (view == null) {
                viewHolder = new ViewHolder();

                view = inflater.inflate(R.layout.dish_grid_item, null);

                viewHolder.rl_main = (FrameLayout) view.findViewById(R.id.rl_main);
                viewHolder.mainImg = (ScaleImageView) view.findViewById(R.id.mainImg);
                viewHolder.name = (TextView) view.findViewById(R.id.name);
                viewHolder.style = (TextView) view.findViewById(R.id.style);
                viewHolder.during = (TextView) view.findViewById(R.id.during);

                viewHolder.rl_main.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        goDetail(dish.getId());
                    }
                });

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.name.setText(dish.getName());
            viewHolder.style.setText(dish.getStyle());
            viewHolder.during.setText(dish.getDuring());

            String mainpicStr = dish.getMainPic();
            mainpicStr = mainpicStr.substring(mainpicStr.lastIndexOf("/") + 1);

            //从缓存中获取图片，固定ImageView大小，解决图片大小频繁变化的问题
            Bitmap bitmap = null;
            try {
                bitmap = fb.getBitmapFromCache(Constant.DIR + mainpicStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (bitmap != null) {

                viewHolder.mainImg.setImageWidth(bitmap.getWidth());
                viewHolder.mainImg.setImageHeight(bitmap.getHeight());

            }

            fb.display(viewHolder.mainImg, Constant.DIR + mainpicStr);

            return view;
        }

        class ViewHolder {
            FrameLayout rl_main;
            ScaleImageView mainImg;
            TextView name;
            TextView style;
            TextView during;
        }
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
            waterfallView.setRefreshTime("第一次刷新");
        } else {
            waterfallView.setRefreshTime(StringUtil.friendly_time(lastRefreshTime));
        }

        spfu.saveSharedPreferences("lastRefreshTime", StringUtil.gettimenow());
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

}
