package com.sky.cookbooksa.uihelper;

import com.sky.cookbooksa.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

public class CommonDialogHelper {

	private Context context;
	private View view;
	private boolean cancelable;

	private Dialog dialog;

	public CommonDialogHelper(){}

	public CommonDialogHelper(Context context, View view, boolean cancelable){

		this.context = context;
		this.view = view;
		this.cancelable = cancelable;

		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		dialog = new Dialog(context, R.style.CustomDialogTheme);
		dialog.setContentView(view);
		dialog.setCancelable(cancelable);
	}

	public void showDialog(){
		if(!dialog.isShowing()){
			dialog.show();
		}
	}

	public void dismissDialog(){
		if(dialog.isShowing()){
			dialog.dismiss();
		}
	}

}
