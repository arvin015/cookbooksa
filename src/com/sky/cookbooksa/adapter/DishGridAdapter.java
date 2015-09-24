package com.sky.cookbooksa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sky.cookbooksa.R;
import com.sky.cookbooksa.entity.Dish;
import com.sky.cookbooksa.utils.Constant;

import net.tsz.afinal.FinalBitmap;

import java.util.List;

public class DishGridAdapter extends BaseAdapter {

    private Context context;
    private List<Dish> dishs;
    private FinalBitmap fb;

    public DishGridAdapter(Context context, List<Dish> dishs) {
        this.context = context;
        this.dishs = dishs;
        fb = FinalBitmap.create(context);
        fb.configLoadfailImage(R.drawable.photo_loading);
        fb.configLoadingImage(R.drawable.photo_loading);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dishs.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return dishs.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.dish_grid_item, null);

            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.mainpic);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.during = (TextView) convertView.findViewById(R.id.during);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(dishs.get(position).getName());
        viewHolder.during.setText(dishs.get(position).getDuring());
        fb.display(viewHolder.imageView, Constant.DIR + dishs.get(position).getMainPic());

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView name;
        TextView during;
    }

}
