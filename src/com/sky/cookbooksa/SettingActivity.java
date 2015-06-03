package com.sky.cookbooksa;

import com.sky.cookbooksa.uihelper.CommonDialogHelper;
import com.sky.cookbooksa.utils.SharedPreferencesUtils;
import com.sky.cookbooksa.utils.Utils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingActivity extends BaseActivity{

	private Button exitBtn;
	private ImageButton backBtn;
	private TextView title;

	private View view;

	private CommonDialogHelper dialogHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		init();
	}

	private void init(){

		exitBtn = (Button) findViewById(R.id.exit_login);
		backBtn = (ImageButton) findViewById(R.id.back);
		backBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.title);
		title.setVisibility(View.VISIBLE);
		title.setText("设置");

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				back();
			}
		});

		exitBtn.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if(view == null){
					view = LayoutInflater.from(context).inflate(R.layout.exit_choice, null);

					LinearLayout exitloginContainer = (LinearLayout) view.findViewById(R.id.ll_exitlogin_container);
					LinearLayout exitappContainer = (LinearLayout) view.findViewById(R.id.ll_exitapp_container);
					View line = view.findViewById(R.id.line);

					if(!Utils.isLoaded){
						exitloginContainer.setVisibility(View.GONE);
						line.setVisibility(View.GONE);
					}

					exitloginContainer.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							SharedPreferencesUtils.getInstance(context, "").removeAllKey();
							clearLoginData();

							intentHandle(MainActivity.class, null, true);
						}
					});

					exitappContainer.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							exitApp();
						}
					});
				}

				if(dialogHelper == null){
					dialogHelper = new CommonDialogHelper(context, view, true);
				}

				dialogHelper.showDialog();
			}
		});
	}

	private void clearLoginData(){
		Utils.userNick = "";
		Utils.userPhone = "";
		Utils.userPic = "";
	}
}
