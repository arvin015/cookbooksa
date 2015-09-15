package com.sky.cookbooksa.widget;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.sky.cookbooksa.R;
import com.sky.cookbooksa.entity.Footprint;
import com.sky.cookbooksa.utils.DisplayUtil;
import com.sky.cookbooksa.utils.PopupWindowUtil;

/**
 * Created by arvin.li on 2015/9/11.
 */
public class FootprintView extends LinearLayout {

    private Context context;

    private View childView;

    private Footprint footprint;

    private ImageButton circleBtn;

    private ToggleButton checkBtn;

    public FootprintView(Context context) {
        this(context, null);
    }

    public FootprintView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FootprintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        setOrientation(VERTICAL);
    }

    public void createFootprintView(Footprint footprint, Typeface tf) {
        this.footprint = footprint;

        addView(createLineView());
        addView(createContentView(tf));
    }

    /**
     * 创建连接线
     *
     * @return
     */
    private View createLineView() {

        View view = LayoutInflater.from(context).inflate(R.layout.line, null);

        return view;
    }

    /**
     * 创建内容View
     *
     * @return
     */
    private View createContentView(Typeface tf) {
        final View view = LayoutInflater.from(context).inflate(R.layout.footprint_item, null);

        TextView content = (TextView) view.findViewById(R.id.foot_content);
        TextView time = (TextView) view.findViewById(R.id.foot_time);
        circleBtn = (ImageButton) view.findViewById(R.id.circleBtn);
        checkBtn = (ToggleButton) view.findViewById(R.id.checkBtn);

        content.setTypeface(tf);

        content.setText(Html.fromHtml("你浏览了<font color=\"#FF8000\">【" + footprint.getDishName() + "】菜肴</font>"));
        time.setText(footprint.getFootTime());

        circleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                circleBtn.setVisibility(View.GONE);
                checkBtn.setVisibility(View.VISIBLE);
                checkBtn.setChecked(true);

                listener.onCircleClick(FootprintView.this);
            }
        });

        checkBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCheckClick(FootprintView.this, checkBtn.isChecked());
            }
        });

        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                listener.onclick(footprint);
            }
        });

        view.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub

                listener.onLongClick(FootprintView.this);
//                if (childView == null) {
//                    childView = LayoutInflater.from(context).inflate(R.layout.pup_delete, null);
//                }
//
//                childView.setOnClickListener(new OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        // TODO Auto-generated method stub
//
//                        PopupWindowUtil.getInstance().dismiss();
//
//                        listener.onDeleteClick(FootprintView.this, footprint);
//                    }
//                });
//
//                PopupWindowUtil.getInstance().setPopupWindowSize(ActionBar.LayoutParams.WRAP_CONTENT,
//                        ActionBar.LayoutParams.WRAP_CONTENT);
//                PopupWindowUtil.getInstance().setPopuWindow(childView, -1, null);
//                PopupWindowUtil.getInstance().showAsDropDown(v, view.getMeasuredWidth() / 2 - DisplayUtil.dip2px(20),
//                        -view.getMeasuredHeight() - DisplayUtil.dip2px(40));
                return true;
            }
        });

        return view;
    }

    public ImageButton getCircleBtn() {
        return circleBtn;
    }

    public ToggleButton getCheckBtn() {
        return checkBtn;
    }

    public Footprint getFootprint() {
        return footprint;
    }

    public void setListener(IFootprintViewListener listener) {
        this.listener = listener;
    }

    private IFootprintViewListener listener;

    public static interface IFootprintViewListener {
        void onclick(Footprint footprint);

        void onCircleClick(FootprintView view);

        void onCheckClick(FootprintView view, boolean isChecked);

        void onLongClick(FootprintView view);
    }
}
