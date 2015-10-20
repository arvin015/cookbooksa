/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.waterfull.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.sky.cookbooksa.R;
import com.waterfull.lib.MultiColumnListView;
import com.waterfull.lib.PLA_AbsListView;

public class XMultiListView extends MultiColumnListView implements PLA_AbsListView.OnScrollListener {

    private float mLastY = -1; // save event y
    private Scroller mScroller; // used for scroll back
    private OnScrollListener mScrollListener; // user's scroll listener

    // the interface to trigger refresh and load more.
    private IXListViewListener mListViewListener;

    // -- header view
    private XListViewHeader mHeaderView;
    // header view content, use it to calculate the Header's height. And hide it
    // when disable pull refresh.
    private RelativeLayout mHeaderViewContent;
    private TextView mHeaderTimeView;
    private int mHeaderViewHeight; // header view's height
    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false; // is refreashing.

    // -- footer view
    private XListViewFooter mFooterView;
    private boolean mEnablePullLoad;
    private boolean mPullLoading;
    private boolean mIsFooterReady = false;

    // total list items, used to detect is at the bottom of listview.
    private int mTotalItemCount;

    // for mScroller, scroll back from header or footer.
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 400; // scroll back duration
    private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
    // at bottom, trigger
    // load more.
    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
    // feature.

    private boolean isFirstSetRefreshTime = true; // first refresh time

    /**
     * @param context
     */
    public XMultiListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public XMultiListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public XMultiListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    /**
     * init
     *
     * @param context
     */
    private void initWithContext(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        // XListView need the scroll event, and it will dispatch the event to
        // user's listener (as a proxy).

        // init header view
        mHeaderView = new XListViewHeader(context);
        mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(R.id.xlistview_header_content);
        mHeaderTimeView = (TextView) mHeaderView.findViewById(R.id.xlistview_header_time);
        addHeaderView(mHeaderView);

        // init footer view
        mFooterView = new XListViewFooter(context);

        // init header height
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHeaderViewHeight = mHeaderViewContent.getHeight();
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        super.setOnScrollListener(this);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        // make sure XListViewFooter is the last footer view, and only add once.
        if (mIsFooterReady == false) {
            mIsFooterReady = true;
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    /**
     * add other header view
     *
     * @param view
     */
    public void addMoreHeaderView(View view) {
        addHeaderView(view);
    }

    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            mFooterView.hide();
            mFooterView.setOnClickListener(null);
        } else {
            mPullLoading = false;
            mFooterView.show();
            mFooterView.setState(XListViewFooter.STATE_NORMAL);
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;

            isFirstSetRefreshTime = true;

            resetHeaderHeight();
        }
    }

    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading == true) {
            mPullLoading = false;
            mFooterView.setState(XListViewFooter.STATE_NORMAL);
        }
    }

    /**
     * set last refresh time
     *
     * @param time
     */
    public void setRefreshTime(String time) {
        mHeaderTimeView.setText(time);
    }

    /**
     * invoke on scrolling
     */
    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    /**
     * update header view's height
     *
     * @param delta
     */
    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(XListViewHeader.STATE_READY);
            } else {
                mHeaderView.setState(XListViewHeader.STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }

    /**
     * reset header view's height by scroller
     */
    private void resetHeaderHeight() {
        int height = mHeaderView.getVisiableHeight() + 1;

        Log.d("print", "height=" + height);

        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }

        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    /**
     * update footer view's height
     *
     * @param delta
     */
    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
                // more.
                mFooterView.setState(XListViewFooter.STATE_READY);
            } else {
                mFooterView.setState(XListViewFooter.STATE_NORMAL);
            }
        }
        mFooterView.setBottomMargin(height);

        // setSelection(mTotalItemCount - 1); // scroll to bottom
    }

    /**
     * reset footer view's height by scroller
     */
    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            invalidate();
        }
    }

    /**
     * start load more data
     */
    public void startLoadMore() {
        mPullLoading = true;
        mFooterView.setState(XListViewFooter.STATE_LOADING);
        if (mListViewListener != null) {
            mListViewListener.onLoadMore();
        }
    }

    /**
     * get total item count
     *
     * @return
     */
    public int getmTotalItemCount() {
        return mTotalItemCount;
    }

    private boolean canRefresh = false;//
    private boolean isFirst = true;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        //to prevent the down event is not executed
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();

                // viewpager can not trigger the down event, I do not know why???
//                if (getFirstVisiblePositionWithXMView() == 0) {
//                    canRefresh = true;
//                } else {
//                    canRefresh = false;
//                }

                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();

                if (isFirst) {
                    isFirst = false;

                    if (getFirstVisiblePositionWithXMView() == 0) {
                        canRefresh = true;
                    } else {
                        canRefresh = false;
                    }
                }

//                if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
                if (canRefresh &&
                        (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {//arvin edit
                    // the first item is showing, header has shown or pull down.

                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();

                    if (isFirstSetRefreshTime) {

                        mListViewListener.onSetRefreshTime();

                        isFirstSetRefreshTime = false;
                    }

                } else if (getLastVisiblePosition() == mTotalItemCount - 1 && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:

                mLastY = -1;
                canRefresh = false;
                isFirst = true;

                if (getFirstVisiblePosition() == 0) {
                    // invoke refresh
                    if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight
                            && !mPullRefreshing) {
                        mPullRefreshing = true;
                        mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
                        if (mListViewListener != null) {
                            mListViewListener.onRefresh();
                        }
                    }
                    resetHeaderHeight();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1) {
                    // invoke load more.
                    if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA &&
                            !mPullLoading) {
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
        if (mScroller.computeScrollOffset()) {//scroller animation end
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    public XListViewHeader getmHeaderView() {
        return mHeaderView;
    }

    public XListViewFooter getmFooterView() {
        return mFooterView;
    }

    public void setmHeaderView(XListViewHeader mHeaderView) {
        this.mHeaderView = mHeaderView;
    }

    public boolean ismPullRefreshing() {
        return mPullRefreshing;
    }

    public boolean ismPullLoading() {
        return mPullLoading;
    }

    public void setXListViewListener(IXListViewListener l) {
        mListViewListener = l;
    }

    public void setOnScrollListener(OnScrollListener mScrollListener) {
        this.mScrollListener = mScrollListener;
    }

    /**
     * you can listen ListView.OnScrollListener or this one. it will invoke
     * onXScrolling when header/footer scroll back.
     */
    public interface OnXScrollListener extends OnScrollListener {
        public void onXScrolling(View view);
    }

    /**
     * implements this interface to get refresh/load more event/set last refresh time.
     */
    public interface IXListViewListener {
        public void onRefresh();

        public void onLoadMore();

        public void onSetRefreshTime();
    }

    @Override
    public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

    }
}
