package com.waterfull.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.sky.cookbooksa.LuckDrawActivity;
import com.sky.cookbooksa.MainActivity;
import com.sky.cookbooksa.R;
import com.sky.cookbooksa.entity.ActivityInfo;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.DisplayUtil;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.widget.DecoratorViewPager;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by arvin.li on 2015/10/14.
 */
public class XListViewAd extends LinearLayout {

    private MainActivity act;
    private FinalHttp fh;
    private FinalBitmap fb;
    private DecoratorViewPager viewPager;
    private ImageView lastSelectedImage;
    private LinearLayout ll_circle;
    private FrameLayout imageContainer;

    private ArrayList<ImageView> circles;

    private ArrayList<ActivityInfo> images;

    public XListViewAd(Context context) {
        this(context, null);
    }

    public XListViewAd(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XListViewAd(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        act = (MainActivity) context;

        fh = new FinalHttp();

        fb = FinalBitmap.create(context);
        fb.configLoadfailImage(R.drawable.photo_loading);
        fb.configLoadingImage(R.drawable.photo_loading);

        resetImages();
        resetCircles();

        initView();
    }

    private void initView() {

        View view = LayoutInflater.from(act).inflate(R.layout.xlistview_ad, null);

        addView(view);

        viewPager = (DecoratorViewPager) view.findViewById(R.id.image_viewpager);
        ll_circle = (LinearLayout) view.findViewById(R.id.ll_circle_container);
        imageContainer = (FrameLayout) view.findViewById(R.id.imageContainer);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DisplayUtil.screenHeight / 4);
        imageContainer.setLayoutParams(params);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub

                circles.get(arg0).setImageResource(R.drawable.circle_selected);
                lastSelectedImage.setImageResource(R.drawable.circle_unselected);

                lastSelectedImage = circles.get(arg0);
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

        loadData();

    }

    private void loadData() {
        // TODO Auto-generated method stub

        fh.get(Constant.url_activityList, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                // TODO Auto-generated method stub
                super.onFailure(t, errorNo, strMsg);
                ToastUtil.toastShort(act, "活动加载失败=" + strMsg);
            }

            @Override
            public void onSuccess(Object t) {
                // TODO Auto-generated method stub
                super.onSuccess(t);

                JSONObject obj = null;
                try {
                    obj = new JSONObject((String) t);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (obj != null) {

                    ll_circle.removeAllViews();

                    resetCircles();
                    resetImages();

                    JSONArray array = null;
                    try {
                        array = obj.optJSONArray("result");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.optJSONObject(i);
                            ActivityInfo ai = new ActivityInfo(object);
                            images.add(ai);
                        }
                    }
                }

                for (int i = 0; i < images.size(); i++) {
                    ImageView imageView = addCircleImage();
                    if (i == 0) {
                        imageView.setImageResource(R.drawable.circle_selected);
                        lastSelectedImage = imageView;
                    }

                    ll_circle.addView(imageView);
                }

                viewPager.setAdapter(null);
                viewPager.setAdapter(new ImageAdapter());
            }

        });
    }

    public void refresh() {

        loadData();

    }

    private ImageView addCircleImage() {
        ImageView imageView = new ImageView(act);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(15),
                DisplayUtil.dip2px(15));
        params.leftMargin = DisplayUtil.dip2px(5);

        imageView.setLayoutParams(params);

        imageView.setImageResource(R.drawable.circle_unselected);

        circles.add(imageView);

        return imageView;
    }

    private void resetCircles() {
        if (circles == null) {
            circles = new ArrayList<ImageView>();
        }
        circles.clear();
    }

    private void resetImages() {
        if (images == null) {
            images = new ArrayList<ActivityInfo>();
        }
        images.clear();
    }

    class ImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return images.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // TODO Auto-generated method stub

            View view = LayoutInflater.from(act).inflate(R.layout.image_page_item, null);

            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            ProgressBar loading = (ProgressBar) view.findViewById(R.id.loading);
            loading.setVisibility(View.GONE);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    DisplayUtil.screenHeight / 4);
            params.gravity = Gravity.CENTER;
            imageView.setLayoutParams(params);

            fb.display(imageView, Constant.DIR + images.get(position).getActPic());

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(act, LuckDrawActivity.class);
                    intent.putExtra("id", (position + 1) + "");
                    act.startActivity(intent);

                    act.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                }
            });

            ((ViewPager) container).addView(view);

            return view;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

    }
}
