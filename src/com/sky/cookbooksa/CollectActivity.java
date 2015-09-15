package com.sky.cookbooksa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.sky.cookbooksa.adapter.CollectAdapter;
import com.sky.cookbooksa.entity.Collect;
import com.sky.cookbooksa.uihelper.DeleteHelper;
import com.sky.cookbooksa.uihelper.WaitDialogHelper;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.DisplayUtil;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.utils.Utils;
import com.sky.cookbooksa.widget.CustomViewPager;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CollectActivity extends BaseActivity {

    private View mainView;
    private TextView title;
    private ImageButton backBtn;
    private RadioGroup radioGroup;
    private CustomViewPager pager;
    private View dishLine, personLine;

    private List<Collect> dishList, personList, currentList;

    private DeleteHelper deleteHelper;
    private WaitDialogHelper waitDialogHelper;

    private CollectAdapter currentAdapter;

    private int status = 0;//模式，0:查看模式，1:删除模式

    private int deleteFailNum = 0;//删除失败数量
    private int totalDeleteNum = 0;//删除数量
    private HashMap<String, Collect> deleteMap;//删除集合

    public enum AJAX_MODE {
        DISH, PERSON, DELETE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        mainView = LayoutInflater.from(context).inflate(R.layout.collect_main, null);

        setContentView(mainView);

        init();
    }

    private void init() {

        title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        backBtn = (ImageButton) findViewById(R.id.back);
        backBtn.setVisibility(View.VISIBLE);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        pager = (CustomViewPager) findViewById(R.id.viewpager);
        dishLine = (View) findViewById(R.id.line_dish);
        personLine = (View) findViewById(R.id.line_person);

        title.setText("我的收藏");

        pager.setAdapter(new MyAdapter());

        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                back();
            }
        });

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == R.id.radio_dish) {
                    pager.setCurrentItem(0);
                    dishLine.setVisibility(View.VISIBLE);
                    personLine.setVisibility(View.INVISIBLE);

                    currentList = dishList;

                } else {
                    pager.setCurrentItem(1);
                    dishLine.setVisibility(View.INVISIBLE);
                    personLine.setVisibility(View.VISIBLE);

                    currentList = personList;
                }
            }
        });

        pager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                if (arg0 == 0) {
                    dishLine.setVisibility(View.VISIBLE);
                    personLine.setVisibility(View.INVISIBLE);

                    currentList = dishList;

                } else {
                    dishLine.setVisibility(View.INVISIBLE);
                    personLine.setVisibility(View.VISIBLE);

                    currentList = personList;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

        loading("加载中...");

        deleteHelper = new DeleteHelper(context, mainView);
        deleteHelper.setListener(new DeleteHelper.IDeleteHelperListener() {
            /**
             * 取消删除模式
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
                    deleteHelper.setCountText(currentList.size());//设置已选条数
                } else {
                    deleteHelper.setCountText(0);//设置已选条数
                }

                currentAdapter.setCheckBoxState(isChecked);

            }

            @Override
            public void onDeleteClick() {
                doDelete();
            }
        });

    }

    /**
     * 执行删除操作
     */
    private void doDelete() {
        deleteFailNum = 0;

        deleteMap = new HashMap<>();

        //收集删除集合
        for (Collect collect : currentList) {
            if (collect.isChecked()) {

                deleteMap.put(collect.getId() + "", collect);
            }
        }

        totalDeleteNum = deleteMap.size();

        if (totalDeleteNum < 1) {
            ToastUtil.toastShort(context, "未选中删除对象！");
            return;
        }

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

        //删除
        for (String key : deleteMap.keySet()) {
            AjaxParams params = new AjaxParams();
            params.put("footId", deleteMap.get(key).getDishId() + "");

            fh.post(Constant.url_deletefootbyfootid, params,
                    new MyAjaxCallback(AJAX_MODE.DELETE, currentAdapter));
        }
    }

    /**
     * 进入删除模式
     */
    private void goDeleteMode() {

        status = 1;//设置成删除模式

        pager.setPagingEnabled(false);//删除模式禁止翻页

        deleteHelper.setDeleteBarVisibility(View.VISIBLE);

        deleteHelper.reset();//重置

        deleteHelper.countNum(1);

        setLayoutParams(50);
    }

    /**
     * 退出删除模式
     */
    private void exitDeleteMode() {

        status = 0;//非删除模式

        pager.setPagingEnabled(true);//查看模式允许翻页

        deleteHelper.setDeleteBarVisibility(View.GONE);

        currentAdapter.setCheckBoxState(false);

        setLayoutParams(10);

    }

    /**
     * 动态设置viewpager底部边距
     *
     * @param bottomMargin
     */
    private void setLayoutParams(int bottomMargin) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.bottomMargin = DisplayUtil.dip2px(bottomMargin);
        pager.setLayoutParams(params);
    }

    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 2;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            ((ViewPager) container).removeViewAt(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // TODO Auto-generated method stub

            View view = LayoutInflater.from(context).inflate(R.layout.collect, null);

            final GridView gridView = (GridView) view.findViewById(R.id.gridview);
            TextView emptyTip = (TextView) view.findViewById(R.id.empty_tip);

            gridView.setColumnWidth((DisplayUtil.getInstance(context).screenWidth - DisplayUtil.dip2px(40)) / 3);

            String url;
            final AJAX_MODE mode;

            List<Collect> list;
            if (position == 0) {
                dishList = new ArrayList<>();
                list = dishList;

                url = Constant.url_getalllovedishbyuserid;
                mode = AJAX_MODE.DISH;
            } else {
                personList = new ArrayList<>();
                list = personList;

                url = Constant.url_getalllovepersonbyuserid;
                mode = AJAX_MODE.PERSON;
            }

            final CollectAdapter adapter = new CollectAdapter(context, list, fb, mode);
            gridView.setAdapter(adapter);

            AjaxParams params = new AjaxParams();
            params.put("userId", Utils.userId);
            fh.post(url, params, new MyAjaxCallback(mode, adapter));

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    if (status == 0) {//查看模式
                        if (position == 0) {
                            Intent intent = new Intent();
                            intent.putExtra("id", dishList.get(i).getDishId() + "");
                            intent.setClass(CollectActivity.this, DishDetailActivity.class);

                            CollectActivity.this.startActivity(intent);
                            CollectActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }
                    } else {//删除模式

                        int result = 1;//选中条数加一
                        if (adapter.getItemCheced(i)) {
                            result = -1;//选中条数减一
                        }

                        deleteHelper.countNum(result);

                        adapter.setItemChecked(i, !adapter.getItemCheced(i));
                        adapter.notifyDataSetChanged();

                        if (deleteHelper.getSelectAllBtn().isChecked()) {
                            deleteHelper.getSelectAllBtn().setChecked(false);
                        } else {
                            if (deleteHelper.getCountNum() == dishList.size()) {
                                deleteHelper.getSelectAllBtn().setChecked(true);
                            }
                        }
                    }
                }
            });

            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                    currentAdapter = adapter;

                    if (status == 0) {
                        goDeleteMode();

                        adapter.setStatus(status);
                        adapter.setItemChecked(i, true);
                        adapter.notifyDataSetChanged();
                    }

                    return false;
                }
            });

            ((ViewPager) container).addView(view);

            return view;
        }
    }

    class MyAjaxCallback extends AjaxCallBack<Object> {

        private List<Collect> collectList;
        private CollectAdapter adapter;

        private AJAX_MODE mode;

        public MyAjaxCallback(AJAX_MODE mode, CollectAdapter adapter) {
            this.mode = mode;
            this.adapter = adapter;
        }

        @Override
        public void onFailure(Throwable t, int errorNo, String strMsg) {
            // TODO Auto-generated method stub
            super.onFailure(t, errorNo, strMsg);

            ToastUtil.toastShort(context, "加载数据失败=" + strMsg);

            loadMissed();
        }

        @Override
        public void onSuccess(Object t) {
            // TODO Auto-generated method stub
            super.onSuccess(t);

            loadMissed();

            JSONObject json = null;

            try {
                json = new JSONObject((String) t);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (json != null) {

                int count = json.optInt("count");

                if (count == 0) {

//                        if (mode == AJAX_MODE.DISH) {
//                            emptyTip.setVisibility(View.VISIBLE);
//                            emptyTip.setText("还没有收藏菜肴哦！");
//                        } else {
//                            emptyTip.setVisibility(View.VISIBLE);
//                            emptyTip.setText("还没有关注的人哦！");
//                        }
                } else {
                    JSONArray arr = null;
                    try {
                        arr = json.optJSONArray("result");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (arr != null) {

                        collectList = new ArrayList<>();

                        for (int i = 0; i < arr.length(); i++) {
                            collectList.add(new Collect(arr.optJSONObject(i)));
                        }

                        if (mode == AJAX_MODE.DISH) {
                            dishList.addAll(collectList);

                            currentList = dishList;

                        } else {
                            personList.addAll(collectList);
                        }

                        adapter.notifyDataSetChanged();
                    }

                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (KeyEvent.KEYCODE_BACK == keyCode & event.getRepeatCount() == 0) {
            if (status == 1) {
                exitDeleteMode();
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
