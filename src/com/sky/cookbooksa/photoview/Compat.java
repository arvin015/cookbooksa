package com.sky.cookbooksa.photoview;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;

public class Compat {
	
	private static final int SIXTY_FPS_INTERVAL = 1000 / 60;
	
	public static void postOnAnimation(View view, Runnable runnable) {
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			SDK9.postOnAnimation(view, runnable);
		} else {
			view.postDelayed(runnable, SIXTY_FPS_INTERVAL);
		}
	}

}
