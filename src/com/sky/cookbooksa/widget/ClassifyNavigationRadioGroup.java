package com.sky.cookbooksa.widget;

import java.util.ArrayList;

import com.sky.cookbooksa.MainActivity;
import com.sky.cookbooksa.R;
import com.sky.cookbooksa.utils.DisplayUtil;
import com.sky.cookbooksa.utils.Utils;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ClassifyNavigationRadioGroup extends RadioGroup{

	private MainActivity act;
	protected int screenWidth, screenHeight;

	private ArrayList<RadioButton> radios;

	public ClassifyNavigationRadioGroup(Context context){
		super(context);

		this.act = (MainActivity)context;
		init();
	}

	public ClassifyNavigationRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		this.act = (MainActivity)context;
		init();
	}

	private void init() {
		// TODO Auto-generated method stub

		screenWidth = DisplayUtil.screenWidth;
		screenHeight = DisplayUtil.screenHeight;

		resetRadios();

		for(int i = 0; i < Utils.navigations.length; i++){
			RadioButton addButton = addButton();

			addButton.setText(Utils.navigations[i]);

			addButton.setTag(i);

			radios.add(addButton);

			this.addView(addButton);
		}
	}

	private RadioButton addButton(){
		final RadioButton imageBtn = new RadioButton(act);

		imageBtn.setGravity(Gravity.CENTER);

		imageBtn.setTextColor(Color.parseColor("#FFFFFF"));

		imageBtn.setTextSize(16);

		imageBtn.setPadding(0, DisplayUtil.dip2px(20), 0, 0);

		imageBtn.setButtonDrawable(android.R.color.transparent);

		RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(LayoutParams.MATCH_PARENT,
				screenHeight / 6 - DisplayUtil.dip2px(30));
		params.bottomMargin = DisplayUtil.dip2px(10);

		imageBtn.setLayoutParams(params);

		imageBtn.setBackgroundResource(R.drawable.navigation_selector);

		return imageBtn;
	}

	public void setRadioChecked(int index){
		radios.get(index).setChecked(true);
	}

	private void resetRadios(){
		if(radios == null){
			radios = new ArrayList<RadioButton>();
		}
		radios.clear();
	}

}
