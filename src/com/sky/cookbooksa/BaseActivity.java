package com.sky.cookbooksa;

import com.sky.cookbooksa.utils.ExitApplication;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class BaseActivity extends Activity {

	protected Context context;

	protected FinalHttp fh;
	protected FinalBitmap fb;

	protected ProgressDialog pb;

	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		init();
	}

	private void init() {
		// TODO Auto-generated method stub

		ExitApplication.getInstance().addActivity(this);

		context = this;

		fh = new FinalHttp();

		fb = FinalBitmap.create(this);
		fb.configLoadfailImage(R.drawable.photo_loading);
		fb.configLoadingImage(R.drawable.photo_loading);
	}

	protected void loading(String msg){
		pb = new ProgressDialog(context);
		pb.setCancelable(true);
		pb.setMessage(msg);
		pb.show();
	}

	protected void loadMissed(){
		if(pb != null && pb.isShowing()){
			pb.dismiss();
		}
	}

	protected void intentHandle(Class<?> to, Bundle bundle, boolean needFinish){
		if(intent == null){
			intent = new Intent();
		}

		if(bundle != null){
			intent.putExtras(bundle);
		}

		intent.setClass(this, to);

		startActivity(intent);

		if(needFinish){
			finish();
		}
	}

	//toggle keyboard
	protected void toggleKeyboard(EditText edit, boolean needOpen){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		if(needOpen){
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
		}else{
			imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
		}
	}

	//finish current Activity
	protected void back(){
		finish();
	}

	//exit app
	protected void exitApp(){
		ExitApplication.getInstance().exit();
	}
}
