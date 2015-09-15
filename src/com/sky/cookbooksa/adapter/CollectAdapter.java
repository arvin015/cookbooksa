package com.sky.cookbooksa.adapter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;

import com.sky.cookbooksa.CollectActivity.AJAX_MODE;
import com.sky.cookbooksa.R;
import com.sky.cookbooksa.entity.Collect;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.DisplayUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class CollectAdapter extends BaseAdapter {

    private Context context;
    private List<Collect> list;

    private FinalBitmap fb;

    private int screenWidth;

    private LayoutInflater inflater;

    private AJAX_MODE mode;

    private int status = 0;//模式，0:查看模式，1:删除模式

    public CollectAdapter(Context context, List<Collect> list,
                          FinalBitmap fb, AJAX_MODE mode) {
        this.context = context;
        this.list = list;
        this.fb = fb;
        this.mode = mode;

        screenWidth = DisplayUtil.screenWidth;

        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.collect_item, null);

            viewHolder.rlImg = (FrameLayout) convertView.findViewById(R.id.rl_img);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageview);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textview);
            viewHolder.checkBtn = (ToggleButton) convertView.findViewById(R.id.checkBtn);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                (int) ((screenWidth - DisplayUtil.dip2px(40)) / 3), (int) ((screenWidth - DisplayUtil.dip2px(40)) / 3));
        viewHolder.rlImg.setLayoutParams(params);

        String path = list.get(position).getMainPic();

        if (mode == AJAX_MODE.DISH) {
            viewHolder.textView.setText(list.get(position).getDishName());

            path = path.substring(path.lastIndexOf("/") + 1);
        } else {
            viewHolder.textView.setText(list.get(position).getUserNick());
        }

        if (status == 1) {//删除模式，显示Checkbox
            viewHolder.checkBtn.setVisibility(View.VISIBLE);
        } else {//查看模式，隐藏Checkbox
            viewHolder.checkBtn.setVisibility(View.GONE);
        }

        viewHolder.checkBtn.setChecked(list.get(position).isChecked());//是否被选中

        fb.display(viewHolder.imageView, Constant.DIR + path,
                (int) ((screenWidth - DisplayUtil.dip2px(40)) / 3), (int) ((screenWidth - DisplayUtil.dip2px(40)) / 3));

        return convertView;
    }

    /**
     * 设置Item是否被选中
     *
     * @param index
     */
    public void setItemChecked(int index, boolean checked) {
        list.get(index).setIsChecked(checked);
    }

    /**
     * 获取Item是否选中状态
     *
     * @param index
     */
    public boolean getItemCheced(int index) {
        return list.get(index).isChecked();
    }

    /**
     * 设置模式
     *
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 设置全选或全不选
     *
     * @param isChecked
     */
    public void setCheckBoxState(boolean isChecked) {
        if (list != null) {
            for (Collect collect : list) {
                collect.setIsChecked(isChecked);
            }
        }

        if (!isChecked) {//隐藏CheckBtn
            status = 0;
        }

        notifyDataSetChanged();
    }

    class ViewHolder {
        FrameLayout rlImg;
        ImageView imageView;
        TextView textView;
        ToggleButton checkBtn;
    }

}
