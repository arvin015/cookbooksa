package com.sky.cookbooksa;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sky.cookbooksa.entity.Footprint;
import com.sky.cookbooksa.uihelper.DeleteHelper;
import com.sky.cookbooksa.uihelper.WaitDialogHelper;
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
import java.util.HashMap;
import java.util.List;

public class FootprintActivity extends BaseActivity {

    private View mainView;
    private CommonScrollView scrollView;
    private LinearLayout footContainer;
    private TextView title, emptyTip;
    private ImageButton backBtn;

    private ArrayList<Footprint> foots;

    private int count;//总共记录数

    private int page = 1;

    private Typeface tf;

    private View loadView;

    private DeleteHelper deleteHelper;
    private WaitDialogHelper waitDialogHelper;

    private boolean isLoading = false;//是否正在加载
    private boolean isFirst = true;

    private List<FootprintView> footprintViewList;

    private int mode = 0;//显示模式，0：查看模式；1：删除模式

    private int deleteFailNum = 0;//删除失败数量
    private int totalDeleteNum = 0;//总共删除数量
    private HashMap<String, FootprintView> delelteMap;//删除对象集合

    private enum AJAX_MODE {
        GET, DELETE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        mainView = LayoutInflater.from(context).inflate(R.layout.footprint, null);

        setContentView(mainView);

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

        deleteHelper = new DeleteHelper(context, mainView);
        deleteHelper.setListener(new DeleteHelper.IDeleteHelperListener() {
            /**
             * 取消删除模式操作
             */
            @Override
            public void onCancelClick() {

                exitDeleteMode();
            }

            /**
             * 全选操作
             * @param isChecked
             */
            @Override
            public void onSelectAllClick(boolean isChecked) {

                if (isChecked) {
                    deleteHelper.setCountText(foots.size());//设置已选条数
                } else {
                    deleteHelper.setCountText(0);//设置已选条数
                }

                setCheckBoxState(isChecked);
            }

            /**
             * 删除操作
             */
            @Override
            public void onDeleteClick() {
                if (waitDialogHelper == null) {
                    waitDialogHelper = new WaitDialogHelper(context, "删除中...");
                    waitDialogHelper.setListener(new WaitDialogHelper.IWaitDialogHelperListener() {
                        @Override
                        public void outTime() {
                            ToastUtil.toastShort(context, "未删除所有对象！");
                        }
                    });
                }
                waitDialogHelper.show();

                doDelete();
            }
        });
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

            if (mode == AJAX_MODE.GET) {

                loadMissed();

                if (loadView != null) {
                    ((ViewGroup) scrollView.getChildAt(0)).removeView(loadView);
                }
                isLoading = false;

                ToastUtil.toastShort(context, "加载足迹失败=" + strMsg);

            } else if (mode == AJAX_MODE.DELETE) {
                deleteFailNum++;
                if (deleteFailNum == totalDeleteNum) {
                    waitDialogHelper.dismiss();
                    ToastUtil.toastShort(context, "删除失败！");
                }
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

                            String footId = json.optString("footId");

                            FootprintView view = delelteMap.get(footId);
                            if (view != null) {
                                footContainer.removeView(view);
                            }

                            count--;
                            totalDeleteNum--;

                            if (totalDeleteNum == 0) {//全部删除
                                waitDialogHelper.dismiss();
                                title.setText("足迹(共" + count + "条)");

                                ToastUtil.toastShort(context, "删除成功");

                                exitDeleteMode();
                            }
                        }
                    }
                }
            }

        }
    }

    /**
     * 删除处理
     */
    private void doDelete() {
        if (footprintViewList == null || footprintViewList.size() == 0) {
            ToastUtil.toastShort(context, "未选中删除对象！");
            return;
        }

        delelteMap = new HashMap<>();

        //收集删除集合
        for (FootprintView view : footprintViewList) {
            if (view.getCheckBtn().isChecked()) {

                totalDeleteNum++;

                delelteMap.put(view.getFootprint().getFootId() + "", view);
            }
        }

        //删除
        for (String key : delelteMap.keySet()) {
            AjaxParams params = new AjaxParams();
            params.put("footId", delelteMap.get(key).getFootprint().getFootId() + "");

            fh.post(Constant.url_deletefootbyfootid, params,
                    new MyAjaxCallback(AJAX_MODE.DELETE));
        }
    }

    /**
     * 设置circle和check显示隐藏
     *
     * @param v1
     * @param v2
     */
    private void setCheckAndCircleBtnVisibility(int v1, int v2) {
        if (footprintViewList != null && footprintViewList.size() > 0) {
            for (FootprintView v : footprintViewList) {
                ToggleButton tBtn = v.getCheckBtn();
                if (tBtn != null) {
                    tBtn.setVisibility(v1);

                    if (View.GONE == v1) {
                        tBtn.setChecked(false);
                    }
                }
                ImageButton circleBtn = v.getCircleBtn();
                if (circleBtn != null) {
                    circleBtn.setVisibility(v2);
                }
            }
        }
    }

    /**
     * 设置选择框状态
     *
     * @param isChecked
     */
    private void setCheckBoxState(boolean isChecked) {
        if (footprintViewList != null && footprintViewList.size() > 0) {
            for (FootprintView v : footprintViewList) {
                v.getCheckBtn().setChecked(isChecked);
            }
        }
    }

    /**
     * 进入删除模式
     */
    private void goDeleteMode() {

        mode = 1;//设置成删除模式

        deleteHelper.setDeleteBarVisibility(View.VISIBLE);

        setCheckAndCircleBtnVisibility(View.VISIBLE, View.GONE);

        deleteHelper.reset();//重置

        deleteHelper.countNum(1);
    }

    /**
     * 退出删除模式
     */
    private void exitDeleteMode() {

        mode = 0;//非删除模式

        deleteHelper.setDeleteBarVisibility(View.GONE);

        setCheckAndCircleBtnVisibility(View.GONE, View.VISIBLE);
    }

    /**
     * 创建FootprintView
     *
     * @param footprint
     * @return
     */
    private FootprintView createFootprintView(Footprint footprint) {
        final FootprintView footprintView = new FootprintView(context);

        footprintView.createFootprintView(footprint, tf);

        footprintView.setListener(new FootprintView.IFootprintViewListener() {
            /**
             * 点击进入事件
             * @param footprint
             */
            @Override
            public void onclick(Footprint footprint) {
                if (mode == 0) {//查看模式，进入菜肴界面
                    Bundle bundle = new Bundle();
                    bundle.putString("id", footprint.getDishId());
                    intentHandle(DishDetailActivity.class, bundle, false);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else if (mode == 1) {//删除模式，checkbox选择
                    boolean isChecked = footprintView.getCheckBtn().isChecked();
                    if (isChecked) {
                        deleteHelper.countNum(-1);//选中条数减一
                    } else {
                        deleteHelper.countNum(1);//选中条数加一
                    }
                    footprintView.getCheckBtn().setChecked(!isChecked);
                }

            }

            /**
             * 点击进入删除模式事件
             * @param view
             */
            @Override
            public void onCircleClick(FootprintView view) {

                goDeleteMode();
            }

            /**
             * Checkbox点击事件
             * @param view
             * @param flag
             */
            @Override
            public void onCheckClick(FootprintView view, int flag) {
                deleteHelper.countNum(flag);
                deleteHelper.setSelectAllBtnFalse();
            }

            /**
             * FootprintView长按事件
             * @param view
             */
            @Override
            public void onLongClick(FootprintView view) {
                if (mode != 1) {//非删除模式可操作

                    view.getCircleBtn().setVisibility(View.GONE);
                    view.getCheckBtn().setVisibility(View.VISIBLE);
                    view.getCheckBtn().setChecked(true);

                    goDeleteMode();
                }
            }
        });

        return footprintView;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode & event.getRepeatCount() == 0) {
            if (mode == 1) {
                exitDeleteMode();//退出删除模式
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
