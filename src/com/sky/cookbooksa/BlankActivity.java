package com.sky.cookbooksa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.sky.cookbooksa.utils.ExitApplication;
import com.sky.cookbooksa.utils.SharedPreferencesUtils;

public class BlankActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.blank);

        ExitApplication.getInstance(this).addActivity(this);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                jumpTo();
            }
        }, 3000);

    }

    private void jumpTo() {

        Class<?> targetClass = null;

        if (SharedPreferencesUtils.getInstance(this, null)
                .loadStringSharedPreference("isFirst") == null) {

            SharedPreferencesUtils.getInstance(this, null).saveSharedPreferences("isFirst", "false");

            targetClass = WelcomeActivity.class;

        } else {

            targetClass = MainActivity.class;
        }

        Intent intent = new Intent(this, targetClass);
        startActivity(intent);

    }
}
