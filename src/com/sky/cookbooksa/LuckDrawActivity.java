package com.sky.cookbooksa;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sky.cookbooksa.utils.RandomUtil;
import com.sky.cookbooksa.utils.SharedPreferencesUtils;
import com.sky.cookbooksa.utils.StringUtil;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.widget.TurntableView;

/**
 * Created by arvin.li on 2015/7/23.
 */
public class LuckDrawActivity extends BaseActivity {

    private ImageButton backBtn;
    private TextView titleText;
    private Button beginBtn;

    private TurntableView turntableView;

    private int currentAreaId;
    private int leftNum = 3;//抽奖剩余次数

    private final int waitingTime = 1;

    private final String[] areaStrs = new String[]{
            "5元红包", "OPPO MP3", "DNF 钱包",
            "谢谢参与", "10元红包", "索尼 PSP GO"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.luck_draw);

        init();
    }

    private void init() {

        backBtn = (ImageButton) findViewById(R.id.back);
        titleText = (TextView) findViewById(R.id.title);
        beginBtn = (Button) findViewById(R.id.beginBtn);
        turntableView = (TurntableView) findViewById(R.id.turntableView);

        backBtn.setVisibility(View.VISIBLE);

        titleText.setText("抽奖");

        beginBtnCheck();

        turntableView.setDefaultAngle(1080, 60);

        turntableView.setListener(new TurntableView.ITurntableListener() {
            @Override
            public void luckDrawEnd(int areaId) {

                if (leftNum > 0) {
                    beginBtn.setEnabled(true);

                    beginBtn.setBackgroundResource(R.drawable.common_yellow_shape);

                    setBeginBtnText("");
                } else {
                    setBeginBtnText("02:00");

                    SharedPreferencesUtils.getInstance(context, "luck").saveSharedPreferences("lastTime",
                            System.currentTimeMillis() + "");
                }

                if (currentAreaId != areaId) {
                    return;
                }

                if (areaId == 4) {
                    ToastUtil.toastShort(context, "很遗憾，没有中奖，再接再厉！");
                } else {
                    ToastUtil.toastShort(context, "恭喜你，获得" + areaStrs[areaId - 1] + "！");
                }

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                back();
            }
        });

        beginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                beginBtn.setEnabled(false);
                beginBtn.setBackgroundResource(R.drawable.common_grey_shape);

                currentAreaId = RandomUtil.randomNum(360 / 60) + 1;

                turntableView.startTurn(currentAreaId);

                leftNum--;
            }
        });
    }

    /**
     * 抽奖状态检测
     */
    private void beginBtnCheck() {

        String lastTime = SharedPreferencesUtils.getInstance(context, "ac").loadStringSharedPreference("lastTime");
        String leftNumS = SharedPreferencesUtils.getInstance(context, "ac").loadStringSharedPreference("leftNum");

        float timeAfter = -1;

        if (lastTime != null || lastTime != "") {
            timeAfter = StringUtil.TimeLeft2(waitingTime, lastTime);
        }

        if (leftNumS != null) {
            leftNum = Integer.parseInt(leftNumS);
        }

        if (timeAfter > 0 && leftNum == 0) {//上次抽完，还在等待中

            setBeginBtnText(String.format("%02d:%02d", (int) (timeAfter / (1000 * 60)),
                    (int) (timeAfter % (1000 * 60) / 1000)));

            beginBtn.setEnabled(false);
            beginBtn.setBackgroundResource(R.drawable.common_grey_shape);

        } else {

            if (timeAfter != -1) {//下次抽奖时间到了

                leftNum = 3;

                SharedPreferencesUtils.getInstance(context, "ac").saveSharedPreferences("lastTime", "");
            }

            setBeginBtnText("");
        }
    }

    /**
     * 设置开始按钮Text
     */
    private void setBeginBtnText(String text) {
        if (leftNum > 0) {
            beginBtn.setText("开始抽奖" + " x" + leftNum);
        } else {
            beginBtn.setText(Html.fromHtml("距离下次抽奖时间还有 <span color=\"#FF00FF\">" + text + "</span>"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferencesUtils.getInstance(context, "ac").saveSharedPreferences("leftNum", leftNum + "");
    }
}
