package com.sky.cookbooksa.uihelper;

import com.sky.cookbooksa.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmDialogHelper {

	private Context context;

	private Dialog dialog;

	private Button cancelBtn, sureBtn;
	private TextView tipText;

	public ConfirmDialogHelper(){}

	public ConfirmDialogHelper(Context context){

		this.context = context;

		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		dialog = new Dialog(context, R.style.CustomDialogTheme);
		dialog.setContentView(R.layout.confirm_dialog);
		dialog.setCancelable(false);

		cancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);
		sureBtn = (Button) dialog.findViewById(R.id.sureBtn);
		tipText = (TextView) dialog.findViewById(R.id.tipText);

		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismissDialog();
			}
		});

		sureBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismissDialog();

				confirmDialogListener.sureHandler();
			}
		});

		showDialog();
	}

	public void setTipText(String tipStr){
		tipText.setText(tipStr);
	}

	public void hiddenCancelBtn(){
		cancelBtn.setVisibility(View.GONE);
	}

	public void hiddenSureBtn(){
		sureBtn.setVisibility(View.GONE);
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

	private IConfirmDialogListener confirmDialogListener;
	public void setListener(IConfirmDialogListener confirmDialogListener){
		this.confirmDialogListener = confirmDialogListener;
	}
	public interface IConfirmDialogListener{
		public void sureHandler();
	}

}
