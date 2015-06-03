package com.sky.cookbooksa.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class CommonListView extends ListView{

	public CommonListView(Context context){
		super(context);
	}

	public CommonListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
				MeasureSpec.AT_MOST);  
		super.onMeasure(widthMeasureSpec, expandSpec);  

	}  
}
