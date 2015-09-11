package com.sky.cookbooksa;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sky.cookbooksa.entity.Footprint;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.utils.Utils;
import com.sky.cookbooksa.widget.CommonScrollView;
import com.sky.cookbooksa.widget.CommonScrollView.OnBorderListener;
import com.sky.cookbooksa.widget.FootprintView;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FootprintActivity extends BaseActivity {

    private CommonScrollView scrollView;
    private LinearLayout footContainer;
    private TextView title, emptyTip;
    private ImageButton backBtn;

    private Button cancelBtn;

    private ArrayList<Footprint> foots;

    private int count;//总共记录数

    private int page = 1;

    private Typeface tf;

    private View loadView;

    private boolean isLoading = false;//是否正在加载
    private boolean isFirst = true;

    private List<FootprintView> footprintViewList;

    private FootprintView currentFootprintView;//当前需删除View

    private enum AJAX_MODE {
        GET, DELETE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.footprint);

        init();
    }

    private void init() {

        loading("数据加载中...");

        foots = new ArrayList<Footprint>();

        footprintViewList = new ArrayList<FootprintView>();

        title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        backBtn = (ImageButton) findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        emptyTip = (TextView) findViewById(R.id.empty_tip);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);

        scrollView = (CommonScrollView) findViewById(R.id.foot_scrollview);
        footContainer = (LinearLayout) findViewById(R.id.foot_container);

        title.setText("我的足迹");

        AssetManager am = context.getAssets();
        tf = Typeface.createFromAsset(am, "font/fzst.ttf");

        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                back();
            }
        });

        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setDeleteBarVisibility(View.GONE);

                setCheckAndCircleBtnVisibility(View.VISIBLE, View.GONE);
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

                if (isLoading) {
                    return;
                }

                //已经加载完
                if (count <= foots.size()) {

                    if (isFirst) {
                        ToastUtil.toastShort(context, "数据全部加载完毕！");
                        isFirst = false;
                    }

                    return;
                }

                if (loadView == null) {
                    loadView = LayoutInflater.from(context).inflate(R.layout.loadingmore, null);
                }

                ViewGroup viewGroup = (ViewGroup) loadView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(loadView);
                }
                ((ViewGroup) scrollView.getChildAt(0)).addView(loadView);

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

                loadData();

                isLoading = true;
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        loadMissed();
    }

    class MyAjaxCallback extends AjaxCallBack<Object> {

        private AJAX_MODE mode;

        public MyAjaxCallback(AJAX_MODE mode) {
            this.mode = mode;
        }

        @Override
        public void onFailure(Throwable t, int errorNo, String strMsg) {
            // TODO Auto-generated method stub
            super.onFailure(t, errorNo, strMsg);

            loadMissed();

            if (mode == AJAX_MODE.GET) {

                if (loadView != null) {
                    ((ViewGroup) scrollView.getChildAt(0)).removeView(loadView);
                }
                isLoading = false;

                ToastUtil.toastShort(context, "加载足迹失败=" + strMsg);
            } else if (mode == AJAX_MODE.DELETE) {
                ToastUtil.toastShort(context, "删除足迹失败=" + strMsg);
            }
        }

        @Override
        public void onSuccess(Object t) {
            // TODO Auto-generated method stub
            super.onSuccess(t);

            JSONObject json = null;
            try {
                json = new JSONObject((String) t);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (json != null) {
                if (mode == AJAX_MODE.GET) {

                    loadMissed();
                    if (loadView != null) {
                        ((ViewGroup) scrollView.getChildAt(0)).removeView(loadView);
                    }
                    isLoading = false;

                    count = json.optInt("count");

                    title.setText("足迹(共" + count + "条)");

                    if (count < 1) {
                        emptyTip.setVisibility(View.VISIBLE);
                    }

                    JSONArray arr = null;
                    try {
                        arr = json.optJSONArray("result");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (arr != null) {

                        ArrayList<Footprint> list = new ArrayList<Footprint>();

                        for (int i = 0; i < arr.length(); i++) {
                            list.add(new Footprint(arr.optJSONObject(i)));
                        }

                        for (int j = 0; j < list.size(); j++) {

                            FootprintView footprintView = createFootprintView(list.get(j));

                            footprintViewList.add(footprintView);

                            footContainer.addView(footprintView);
                        }

                        foots.addAll(list);

                        page++;
                    }
                } else {
                    if (mode == AJAX_MODE.DELETE) {
                        String state = json.optString("state");
                        if ("true".equals(state)) {
                            ToastUtil.toastShort(context, "删除成功");

                            count--;

                            title.setText("足迹(共" + count + "条)");

                            if (currentFootprintView != null) {
                                footContainer.removeView(currentFootprintView);
                            }

                        } else {
                            ToastUtil.toastShort(context, "删除失败");
                        }
                    }
                }
            }

        }
    }

    /**
     * 显示隐藏删除bar
     *
     * @param visibility
     */
    public void setDeleteBarVisibility(int visibility) {
        FrameLayout topContainer = (FrameLayout) findViewById(R.id.topContainer);
        FrameLayout bottomContainer = (FrameLayout) findViewById(R.id.bottomContainer);

        topContainer.setVisibility(visibility);
        bottomContainer.setVisibility(visibility);
    }

    /**
     * 设置circle和check显示隐藏
     *
     * @param v1
     * @param v2
     */
    public void setCheckAndCircleBtnVisibility(int v1, int v2) {
        if (footprintViewList != null && footprintViewList.size() > 0) {
            for (FootprintView v : footprintViewList) {
                ToggleButton tBtn = v.getCheckBtn();
                if (tBtn != null) {
                    tBtn.setVisibility(v1);
                }
                ImageButton circleBtn = v.getCircleBtn();
                if (circleBtn != null) {
                    circleBtn.setVisibility(v2);
                }
            }
        }
    }

    /**
     * 创建FootprintView
     *
     * @param footprint
     * @return
     */
    private FootprintView createFootprintView(Footprint footprint) {
        FootprintView footprintView = new FootprintView(context);

        footprintView.createFootprintView(footprint, tf);

        footprintView.setListener(new FootprintView.IFootprintViewListener() {
            /**
             * 点击进入事件
             * @param footprint
             */
            @Override
            public void onclick(Footprint footprint) {
                Bundle bundle = new Bundle();
                bundle.putString("id", footprint.getDishId());
                intentHandle(DishDetailActivity.class, bundle, false);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

            /**
             * 点击进入删除模式事件
             * @param view
             */
            @Override
            public void onCircleClick(FootprintView view) {

                setDeleteBarVisibility(View.VISIBLE);

                setCheckAndCircleBtnVisibility(View.VISIBLE, View.GONE);
            }

            /**
             * 点击删除事件
             * @param view
             * @param footprint
             */
            @Override
            public void onDeleteClick(FootprintView view, Footprint footprint) {

                currentFootprintView = view;

                AjaxParams params = new AjaxParams();
                params.put("footId", footprint.getFootId() + "");

                fh.post(Constant.url_deletefootbyfootid, params,
                        new MyAjaxCallback(AJAX_MODE.DELETE));
            }
        });

        return footprintView;
    }

}
