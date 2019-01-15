package com.example.slipwindow.View;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.slipwindow.MainActivity;
import com.example.slipwindow.R;
import com.example.slipwindow.util.ProgressManager;
import com.zjun.progressbar.CircleDotProgressBar;

import java.util.List;

/**
 * Created by asus on 2017-05-09.
 */

public class SusWindowBigView extends LinearLayout {

    public static int viewWidth;//记录大悬浮窗的宽度

    public static int viewHeight;//记录大悬浮窗的高度

    private Button clearButton;//清理按钮

    public SusWindowBigView(final Context context){
        super(context);
        LayoutInflater.from(context).inflate(R.layout.sus_window_big, this);
        View view = findViewById(R.id.sus_window_big_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;//
        Button backButton=(Button)findViewById(R.id.sus_window_back);
        clearButton=(Button)findViewById(R.id.sus_window_clear);
      //  CircleDotProgressBar circleDotProgressBar=(CircleDotProgressBar)findViewById(R.id.bar_percent);

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击返回的时候，移除大悬浮窗，创建小悬浮窗
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });

        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int count=0;
              //  PackageManager packageManager=context.getPackageManager();
                ActivityManager activityManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ProgressManager.Process> runningProcesses=ProgressManager.getRunningProcesses();//获取运行进程
                for(ProgressManager.Process process:runningProcesses) {
                    if (!process.getPackageName().equals(context.getPackageName())) {
                        activityManager.killBackgroundProcesses(process.getPackageName());
                        count++;
                    }
                }
                List<ProgressManager.Process> runningProcesses1=ProgressManager.getRunningProcesses();//获取运行进程
                if(count>runningProcesses1.size()){
                    Toast.makeText(context,"成功清理掉"+String.valueOf(count-runningProcesses1.size())+"个进程",Toast.LENGTH_SHORT).show ();
                }else{
                    Toast.makeText(context,"已达最佳,无需清理",Toast.LENGTH_SHORT).show ();
                }

            }
        });
        

    }


}
