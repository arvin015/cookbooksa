package com.sky.cookbooksa;

import com.sky.cookbooksa.utils.SharedPreferencesUtils;

import android.os.Bundle;
import android.os.Handler;

public class BlankActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.blank);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				jumpTo();
			}
		}, 3000);

	}

	private void jumpTo(){
		Class<?> targetClass = null;

		if(SharedPreferencesUtils.getInstance(context, null)
				.loadStringSharedPreference("isFirst") == null){

			SharedPreferencesUtils.getInstance(context, null).saveSharedPreferences("isFirst", "false");

			targetClass = WelcomeActivity.class;

		}else{

			targetClass = MainActivity.class;
		}

		intentHandle(targetClass, null, true);
	}
}
