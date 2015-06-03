package com.sky.cookbooksa.uihelper;

import com.sky.cookbooksa.R;
import com.sky.cookbooksa.utils.DisplayUtil;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class ListDialogHelper {

	private Context context;

	private LinearLayout listContainer;
	private Button closeBtn;
	private Dialog dialog;

	private String[] items;

	public ListDialogHelper(Context context,
			IListDialogHelperListener listener){
		this.context = context;
		this.listener = listener;

		init();
	}

	private void init() {
		// TODO Auto-generated method stub

		dialog = new Dialog(context, R.style.CustomDialogTheme);

		View view = LayoutInflater.from(context).inflate(R.layout.list_dialog, null);

		LayoutParams params = new LayoutParams(
				(int) (DisplayUtil.screenWidth * 0.9), LayoutParams.WRAP_CONTENT);

		dialog.setContentView(view, params);

		dialog.setCancelable(false);

		listContainer = (LinearLayout) dialog.findViewById(R.id.listContainer);

		closeBtn = (Button) dialog.findViewById(R.id.closeBtn);

		closeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismissDialog();
			}
		});
	}

	public void setItems(String[] items){

		this.items = items;

		for(int i = 0; i < items.length; i++){
			listContainer.addView(createButton(i));
		}

		showDialog();
	}

	public void showDialog(){
		if(dialog != null){
			dialog.show();
		}
	}

	public void dismissDialog(){
		if(dialog != null){
			if(dialog.isShowing()){
				dialog.dismiss();
			}
		}
	}

	private Button createButton(final int item){

		Button button = new Button(context);
		button.setBackgroundResource(R.drawable.common_white_bg);
		button.setText(items[item]);
		button.setTextSize(20);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		params.topMargin = 10;
		button.setLayoutParams(params);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listener.clickHandle(item);

				dismissDialog();
			}
		});

		return button;
	}

	private IListDialogHelperListener listener;

	public interface IListDialogHelperListener{
		public void clickHandle(int position);
	}
}
