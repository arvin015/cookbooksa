package com.sky.cookbooksa.uihelper;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sky.cookbooksa.R;

/**
 * Created by Administrator on 2015/9/12.
 */
public class BatchDeleteHelper {

    private Context context;

    private View mainView;
    private FrameLayout topContainer, bottomContainer;
    private Button cancelBtn;
    private TextView countText;
    private ToggleButton selectAllBtn;
    private Button deleteBtn;

    private Animation topInAnim, bottomInAnim;
    private Animation topOutAnim, bottomOutAnim;

    private int countNum = 0;

    public BatchDeleteHelper() {
    }

    public BatchDeleteHelper(Context context, View view) {
        this.context = context;
        this.mainView = view;

        init();
    }

    private void init() {
        topContainer = (FrameLayout) mainView.findViewById(R.id.topContainer);
        bottomContainer = (FrameLayout) mainView.findViewById(R.id.bottomContainer);
        cancelBtn = (Button) mainView.findViewById(R.id.cancelBtn);
        deleteBtn = (Button) mainView.findViewById(R.id.deleteBtn);
        countText = (TextView) mainView.findViewById(R.id.countText);
        selectAllBtn = (ToggleButton) mainView.findViewById(R.id.selectAllBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onCancelClick();
            }
        });

        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onSelectAllClick(selectAllBtn.isChecked());
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onDeleteClick();
            }
        });
    }

    /**
     * 数据重新设置
     */
    public void reset() {
        countNum = 0;
        setCountText(countNum);
        selectAllBtn.setChecked(false);
    }

    /**
     * 设置已选中条数
     *
     * @param count
     */
    public void setCountText(int count) {
        this.countNum = count;
        countText.setText(Html.fromHtml("已选中 <font color=\"#FFF000\">" + count + "</font> 条"));
    }

    /**
     * 加减选中条数
     *
     * @param flag -1：减一，1：加一
     */
    public void countNum(int flag) {
        countNum += flag;

        setCountText(countNum);
    }

    /**
     * 获取选中条数
     *
     * @return
     */
    public int getCountNum() {
        return countNum;
    }

    public ToggleButton getSelectAllBtn() {
        return selectAllBtn;
    }

    /**
     * 改变全选状态
     */
    public void setSelectAllBtn(boolean isChecked) {
        selectAllBtn.setChecked(isChecked);
    }

    /**
     * 显示隐藏删除bar
     *
     * @param visibility
     */
    public void setDeleteBarVisibility(int visibility) {

        if (visibility == View.VISIBLE) {
            if (topInAnim == null) {
                topInAnim = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_top);
            }

            if (bottomInAnim == null) {
                bottomInAnim = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_bottom);
            }

            topContainer.setAnimation(topInAnim);
            topInAnim.start();

            bottomContainer.setAnimation(bottomInAnim);
            bottomInAnim.start();

        } else {
            if (topOutAnim == null) {
                topOutAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_to_top);
            }

            if (bottomOutAnim == null) {
                bottomOutAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_to_bottom);
            }

            topContainer.setAnimation(topOutAnim);
            topOutAnim.start();

            bottomContainer.setAnimation(bottomOutAnim);
            bottomOutAnim.start();
        }

        topContainer.setVisibility(visibility);
        bottomContainer.setVisibility(visibility);
    }

    private IDeleteHelperListener listener;

    public void setListener(IDeleteHelperListener listener) {
        this.listener = listener;
    }

    public static interface IDeleteHelperListener {
        void onCancelClick();

        void onSelectAllClick(boolean isChecked);

        void onDeleteClick();
    }

}
