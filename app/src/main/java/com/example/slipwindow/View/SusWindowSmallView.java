package com.example.slipwindow.View;

import android.app.ActivityManager;
import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.MessageFormat;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slipwindow.R;
import com.example.slipwindow.util.ProgressManager;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by asus on 2017-05-09.
 */

public class SusWindowSmallView extends LinearLayout {

    public static int viewWidth;//记录小悬浮窗的宽度
    public static int viewHeight;//记录小悬浮窗的高度
    private static int statusBarHeight;//记录系统状态栏的高度
    private WindowManager windowManager;//用于更新小悬浮窗的位置
    private LinearLayout smallWindowLayout;//小悬浮窗的布局
    private WindowManager.LayoutParams mParams;//小悬浮窗的参数
    private float xInScreen;//记录当前手指位置在屏幕上的横坐标值
    private float yInScreen;//记录当前手指位置在屏幕上的纵坐标值
    private  int rocketWidth;//记录小火箭的宽度
    private int rocketHeight;//火箭高度
    private ImageView rocketImg;//火箭控件
    private float xDownInScreen;//记录手指按下时在屏幕上的横坐标的值
    private float yDownInScreen;//记录手指按下时在屏幕上的纵坐标的值
    private float xInView;//记录手指按下时在小悬浮窗的View上的横坐标的值
    private float yInView;//记录手指按下时在小悬浮窗的View上的纵坐标的值
    private boolean isPressed;//记录当前手指是否按下
    private Context context;

    public SusWindowSmallView(Context context){
        super(context);
        this.context=context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.sus_window_small, this);
        //View view = findViewById(R.id.sus_window_small_layout);
        //viewWidth = view.getLayoutParams().width;
        //viewHeight = view.getLayoutParams().height;
        smallWindowLayout=(LinearLayout)findViewById(R.id.sus_window_small_layout);
        viewWidth = smallWindowLayout.getLayoutParams().width;
        viewHeight = smallWindowLayout.getLayoutParams().height;
        rocketImg = (ImageView) findViewById(R.id.rocket_img);
        rocketWidth = rocketImg.getLayoutParams().width;
        rocketHeight = rocketImg.getLayoutParams().height;
        TextView percentView = (TextView) findViewById(R.id.percent);
        percentView.setText(MyWindowManager.getUsedPercentValue(context));

    }

    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isPressed=true;
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                // 手指移动的时候更新小悬浮窗的位置
                updateViewStatus();
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                isPressed=false;
                if (MyWindowManager.isReadyToLaunch()) {
                    launchRocket();
                  //  clearTask(context);//清理任务

                }else{
                    updateViewStatus();
                    // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                    if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
                        openBigWindow();
                    }
                }

                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 发射小火箭
     */
    private void launchRocket() {
        MyWindowManager.removeLauncher(getContext());
        new LaunchTask().execute();
    }


    /**
     * 火箭发射后清理任务
     */
    public void clearTask(Context context){
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


    /**
     * 更新View的显示状态，判断是显示悬浮窗还是小火箭
     */
    private void updateViewStatus() {
        if (isPressed && rocketImg.getVisibility() != View.VISIBLE) {
            mParams.width = rocketWidth;
            mParams.height = rocketHeight;
            windowManager.updateViewLayout(this, mParams);
            smallWindowLayout.setVisibility(View.GONE);
            rocketImg.setVisibility(View.VISIBLE);
            MyWindowManager.createLauncher(getContext());
        } else if (!isPressed) {
            mParams.width = viewWidth;
            mParams.height = viewHeight;
            windowManager.updateViewLayout(this, mParams);
            smallWindowLayout.setVisibility(View.VISIBLE);
            rocketImg.setVisibility(View.GONE);
            MyWindowManager.removeLauncher(getContext());
        }
    }


    /**
     * 小悬浮窗参数传入，用于更新小悬浮窗位置
     * @param params
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕的位置
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 打开大悬浮窗，同时关闭小悬浮窗。
     */
    private void openBigWindow() {
        MyWindowManager.createBigWindow(getContext());
        MyWindowManager.removeSmallWindow(getContext());
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }


    class LaunchTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // 在这里对小火箭的位置进行改变，从而产生火箭升空的效果
            while (mParams.y > 0) {
                mParams.y = mParams.y - 10;
                publishProgress();
                try {
                    Thread.sleep(8);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            windowManager.updateViewLayout(SusWindowSmallView.this, mParams);
        }

        @Override
        protected void onPostExecute(Void result) {
            // 火箭升空结束后，回归到悬浮窗状态
            updateViewStatus();
            clearTask(context);//清理任务
            mParams.x = (int) (xDownInScreen - xInView);
            mParams.y = (int) (yDownInScreen - yInView);
            windowManager.updateViewLayout(SusWindowSmallView.this, mParams);
        }

    }


}
