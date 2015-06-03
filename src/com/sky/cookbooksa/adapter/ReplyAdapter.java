package com.sky.cookbooksa.adapter;

import java.util.ArrayList;

import com.sky.cookbooksa.R;
import com.sky.cookbooksa.entity.Reply;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ReplyAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<Reply> replies;
	private LayoutInflater inflater;

	public ReplyAdapter(Context context, ArrayList<Reply> replies){
		this.context = context;
		this.replies = replies;

		inflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return replies.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return replies.get(position);
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
		if(convertView == null){
			viewHolder = new ViewHolder();

			convertView = inflater.inflate(R.layout.reply_item, null);
			viewHolder.content = (TextView) convertView.findViewById(R.id.content);

			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.content.setText(Html.fromHtml("<font color=\"#0000FF\">"+
				replies.get(position).getUserNick()+"：</font>"+replies.get(position).getReplyContent()));

		return convertView;
	}

	class ViewHolder{
		TextView content;
	}

}
