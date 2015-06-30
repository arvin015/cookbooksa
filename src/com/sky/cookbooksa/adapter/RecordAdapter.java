package com.sky.cookbooksa.adapter;

import java.util.List;

import net.tsz.afinal.FinalDb;

import com.sky.cookbooksa.MainActivity;
import com.sky.cookbooksa.R;
import com.sky.cookbooksa.entity.SRecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecordAdapter extends BaseAdapter{

	private MainActivity context;
	private List<SRecord> records;
	private FinalDb fd;

	public RecordAdapter(Context context, List<SRecord> records, FinalDb fd){
		this.context = (MainActivity)context;
		this.records = records;
		this.fd = fd;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return records.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return records.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder viewHolder;

		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.record, null);
			viewHolder.ll_record_container = (LinearLayout)convertView.findViewById(R.id.ll_record_container);
			viewHolder.content = (TextView)convertView.findViewById(R.id.record_content);
			viewHolder.delete = (Button)convertView.findViewById(R.id.record_delete);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.content.setText(records.get(position).getContent());
		viewHolder.ll_record_container.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				callback.itemClickHandle(records.get(position).getContent());
			}
		});
		viewHolder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				fd.deleteById(SRecord.class, records.get(position).getId());//从本地数据库中删除

				records.remove(position);
				notifyDataSetChanged();
				if(records.size() < 1){
					callback.deleteAllCompleted();
				}
			}
		});

		return convertView;
	}

	class ViewHolder{
		LinearLayout ll_record_container;
		TextView content;
		Button delete;
	}

	private ICallback callback;

	public void setListener(ICallback callback){
		this.callback = callback;
	}

	public interface ICallback{
		public void deleteAllCompleted();
		public void itemClickHandle(String key);
	}

}
