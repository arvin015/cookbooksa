package com.waterfull.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.sky.cookbooksa.R;

/**
 * Created by arvin.li on 2015/10/15.
 */
public class XListView extends ListView {

    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;
    // 滑动时长
    private final static int SCROLL_DURATION = 400;
    // 加载更多的距离
    private final static int PULL_LOAD_MORE_DELTA = 100;
    // 滑动比例
    private final static float OFFSET_RADIO = 2f;
    // 记录按下点的y坐标
    private float lastY;
    // 用来回滚
    private Scroller scroller;
    private IXListViewListener mListViewListener;
    private XListViewHeader headerView;
    private RelativeLayout headerViewContent;
    private TextView headerViewTime;
    // header的高度
    private int headerHeight;
    // 是否能够刷新
    private boolean enableRefresh = true;
    // 是否正在刷新
    private boolean isRefreashing = false;
    // footer
    private XListViewFooter footerView;
    // 是否可以加载更多
    private boolean enableLoadMore;
    // 是否正在加载
    private boolean isLoadingMore;
    // 是否footer准备状态
    private boolean isFooterAdd = false;
    // total list items, used to detect is at the bottom of listview.
    private int totalItemCount;
    // 记录是从header还是footer返回
    private int mScrollBack;
    // 此次上拉是否是第一次
    private boolean isFirstSetRefreshTime = true;

    public XListView(Context context) {
        super(context);
        initView(context);
    }

    public XListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public XListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void initView(Context context) {

        scroller = new Scroller(context, new DecelerateInterpolator());

        headerView = new XListViewHeader(context);
        footerView = new XListViewFooter(context);

        headerViewContent = (RelativeLayout) headerView
                .findViewById(R.id.xlistview_header_content);
        headerViewTime = (TextView) headerView.findViewById(R.id.xlistview_header_time);

        headerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        headerHeight = headerViewContent.getHeight();
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
        addHeaderView(headerView);

    }

    /**
     * 加入其它头部View
     *
     * @param view
     */
    public void addMoreHeaderView(View view) {
        addHeaderView(view);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        // 确保footer最后添加并且只添加一次
        if (isFooterAdd == false) {
            isFooterAdd = true;
            addFooterView(footerView);
        }
        super.setAdapter(adapter);

    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    public boolean isRefreashing() {
        return isRefreashing;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        totalItemCount = getAdapter().getCount();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录按下的坐标
                lastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 计算移动距离
                float deltaY = ev.getRawY() - lastY;
                lastY = ev.getRawY();
                // 是第一项并且标题已经显示或者是在下拉
                if (getFirstVisiblePosition() == 0
                        && (headerView.getVisiableHeight() > 0 || deltaY > 0)) {
                    updateHeaderHeight(deltaY / OFFSET_RADIO);

                    // 该设置上次刷新时间了
                    if (isFirstSetRefreshTime) {

                        if (mListViewListener != null) {
                            mListViewListener.onSetRefreshTime();
                        }

                        isFirstSetRefreshTime = false;
                    }

                } else if (getLastVisiblePosition() == totalItemCount - 1
                        && (footerView.getBottomMargin() > 0 || deltaY < 0)) {
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;

            case MotionEvent.ACTION_UP:

                if (getFirstVisiblePosition() == 0) {
                    if (enableRefresh
                            && headerView.getVisiableHeight() > headerHeight
                            && !isRefreashing) {

                        isRefreashing = true;
                        headerView.setState(XListViewHeader.STATE_REFRESHING);
                        if (mListViewListener != null) {
                            mListViewListener.onRefresh();
                        }
                    }
                    resetHeaderHeight();
                } else if (getLastVisiblePosition() == totalItemCount - 1) {
                    if (enableLoadMore
                            && footerView.getBottomMargin() > PULL_LOAD_MORE_DELTA
                            && !isLoadingMore) {
                        startLoadMore();
                    }
                    resetFooterHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {

        // 松手之后调用
        if (scroller.computeScrollOffset()) {

            if (mScrollBack == SCROLLBACK_HEADER) {
                headerView.setVisiableHeight(scroller.getCurrY());
            } else {
                footerView.setBottomMargin(scroller.getCurrY());
            }
            postInvalidate();
        }
        super.computeScroll();

    }

    /**
     * 设置是否可下拉刷新
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        enableRefresh = enable;

        if (!enableRefresh) {
            headerView.hide();
        } else {
            headerView.show();
        }
    }

    /**
     * 设置是否可加载更多
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        enableLoadMore = enable;
        if (!enableLoadMore) {
            footerView.hide();
            footerView.setOnClickListener(null);
        } else {
            isLoadingMore = false;
            footerView.show();
            footerView.setState(XListViewFooter.STATE_NORMAL);
            footerView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * 停止刷新
     */
    public void stopRefresh() {
        if (isRefreashing == true) {
            isRefreashing = false;
            isFirstSetRefreshTime = true;
            resetHeaderHeight();
        }
    }

    /**
     * 停止加载更多
     */
    public void stopLoadMore() {
        if (isLoadingMore == true) {
            isLoadingMore = false;
            footerView.setState(XListViewFooter.STATE_NORMAL);
        }
    }

    /**
     * 设置上次刷新时间
     *
     * @param lastTime
     */
    public void setLastRefreshTime(String lastTime) {
        headerViewTime.setText(lastTime);
    }

    /**
     * 更改顶部HeaderView的高度
     *
     * @param delta
     */
    private void updateHeaderHeight(float delta) {
        headerView.setVisiableHeight((int) delta
                + headerView.getVisiableHeight());
        // 未处于刷新状态，更新箭头
        if (enableRefresh && !isRefreashing) {
            if (headerView.getVisiableHeight() > headerHeight) {
                headerView.setState(XListViewHeader.STATE_READY);
            } else {
                headerView.setState(XListViewHeader.STATE_NORMAL);
            }
        }

    }

    /**
     * 回滚顶部上拉的高度
     */
    private void resetHeaderHeight() {
        // 当前的可见高度
        int height = headerView.getVisiableHeight();
        // 如果正在刷新并且高度没有完全展示
        if ((isRefreashing && height <= headerHeight) || (height == 0)) {
            return;
        }
        // 默认会回滚到header的位置
        int finalHeight = 0;
        // 如果是正在刷新状态，则回滚到header的高度
        if (isRefreashing && height > headerHeight) {
            finalHeight = headerHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        // 回滚到指定位置
        scroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        // 触发computeScroll
        invalidate();
    }

    /**
     * 更新FooterView的高度
     *
     * @param delta
     */
    private void updateFooterHeight(float delta) {
        int height = footerView.getBottomMargin() + (int) delta;
        if (enableLoadMore && !isLoadingMore) {
            if (height > PULL_LOAD_MORE_DELTA) {
                footerView.setState(XListViewFooter.STATE_READY);
            } else {
                footerView.setState(XListViewFooter.STATE_NORMAL);
            }
        }
        footerView.setBottomMargin(height);

    }

    /**
     * 回滚底部上拉的高度
     */
    private void resetFooterHeight() {
        int bottomMargin = footerView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            scroller.startScroll(0, bottomMargin, 0, -bottomMargin,
                    SCROLL_DURATION);
            invalidate();
        }
    }

    /**
     * 开始加载更多
     */
    public void startLoadMore() {
        isLoadingMore = true;
        footerView.setState(XListViewFooter.STATE_LOADING);
        if (mListViewListener != null) {
            mListViewListener.onLoadMore();
        }
    }

    public void setXListViewListener(IXListViewListener l) {
        mListViewListener = l;
    }

    public interface IXListViewListener {

        public void onRefresh();

        public void onLoadMore();

        public void onSetRefreshTime();
    }
}
