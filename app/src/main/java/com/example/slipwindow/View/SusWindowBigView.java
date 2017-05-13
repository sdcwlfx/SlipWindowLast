package com.example.slipwindow.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.slipwindow.R;
import com.zjun.progressbar.CircleDotProgressBar;

/**
 * Created by asus on 2017-05-09.
 */

public class SusWindowBigView extends LinearLayout {

    public static int viewWidth;//记录大悬浮窗的宽度

    public static int viewHeight;//记录大悬浮窗的高度

    public SusWindowBigView(final Context context){
        super(context);
        LayoutInflater.from(context).inflate(R.layout.sus_window_big, this);
        View view = findViewById(R.id.sus_window_big_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;//
        Button backButton=(Button)findViewById(R.id.sus_window_back);
        CircleDotProgressBar circleDotProgressBar=(CircleDotProgressBar)findViewById(R.id.bar_percent);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击返回的时候，移除大悬浮窗，创建小悬浮窗
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });
        

    }


}
