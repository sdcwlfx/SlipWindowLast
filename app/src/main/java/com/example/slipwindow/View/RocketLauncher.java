package com.example.slipwindow.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.slipwindow.R;

/**
 * 火箭发射台
 * Created by asus on 2017-05-10.
 */

public class RocketLauncher extends LinearLayout {
    public static int width;//记录火箭发射台的宽度
    public static int height;//记录火箭发射台的高度
    private ImageView launcherImg;//火箭发射台的背景图片

    public RocketLauncher(Context context){
        super(context);
        LayoutInflater.from(context).inflate(R.layout.launcher, this);
        launcherImg = (ImageView) findViewById(R.id.launcher_img);
        width = launcherImg.getLayoutParams().width;
        height = launcherImg.getLayoutParams().height;
    }

    /**
     * 更新火箭发射台的显示状态。如果小火箭被拖到火箭发射台上，就显示发射
     * @param isReadyToLaunch
     */
    public void updateLauncherStatus(boolean isReadyToLaunch) {
        if (isReadyToLaunch) {//发射小火箭
            launcherImg.setImageResource(R.drawable.launcher_bg_fire);
        } else {
            launcherImg.setImageResource(R.drawable.launcher_bg_hold);
        }
    }

}
