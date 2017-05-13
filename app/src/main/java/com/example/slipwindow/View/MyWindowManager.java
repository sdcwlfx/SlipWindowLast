package com.example.slipwindow.View;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.slipwindow.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by asus on 2017-05-09.创建或移除大小悬浮窗
 */

public class MyWindowManager {


    private static RocketLauncher rocketLauncher;
    private static SusWindowBigView susWindowBigView;
    private static SusWindowSmallView susWindowSmallView;
    private static WindowManager.LayoutParams smallWindowParams;
    private static WindowManager.LayoutParams bigWindowParams;
    private static WindowManager.LayoutParams launcherParams;
    private static WindowManager mWindowManager;//用于控制在屏幕上添加或移除悬浮窗
    private static ActivityManager mActivityManager;//用于获取手机可用内存PixelFormat


    /**
     * 创建小悬浮窗
     * @param context
     */
    public static void createSmallWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (susWindowSmallView == null) {
            susWindowSmallView = new SusWindowSmallView(context);
            if (smallWindowParams == null) {
                smallWindowParams = new WindowManager.LayoutParams();
                smallWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                smallWindowParams.format = PixelFormat.RGBA_8888;
                smallWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                smallWindowParams.width = SusWindowSmallView.viewWidth;
                smallWindowParams.height = SusWindowSmallView.viewHeight;
                smallWindowParams.x = screenWidth;
                smallWindowParams.y = screenHeight / 2;
            }
            susWindowSmallView.setParams(smallWindowParams);
            windowManager.addView(susWindowSmallView, smallWindowParams);
        }
    }

    /**
     * 移除小悬浮窗
     * @param context
     */
    public static void removeSmallWindow(Context context) {
        if (susWindowSmallView != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(susWindowSmallView);
            susWindowSmallView = null;
        }
    }

    /**
     * 创建大悬浮窗在屏幕中间
     * @param context
     */
    public static void createBigWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (susWindowBigView == null) {
            susWindowBigView = new SusWindowBigView(context);
            if (bigWindowParams == null) {
                bigWindowParams = new WindowManager.LayoutParams();
                bigWindowParams.x = screenWidth / 2 - SusWindowBigView.viewWidth / 2;
                bigWindowParams.y = screenHeight / 2 - SusWindowBigView.viewHeight / 2;
                bigWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                bigWindowParams.format = PixelFormat.RGBA_8888;
                bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                bigWindowParams.width = SusWindowBigView.viewWidth;
                bigWindowParams.height = SusWindowBigView.viewHeight;
            }
            windowManager.addView(susWindowBigView, bigWindowParams);
        }
    }


    /**
     * 移除大悬浮窗
     * @param context
     */
    public static void removeBigWindow(Context context) {
        if (susWindowBigView != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(susWindowBigView);
            susWindowBigView = null;
        }
    }

    /**
     *  创建一个火箭发射台，位置为屏幕底部
     * @param context
     */
    public static void createLauncher(Context context) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (rocketLauncher == null) {
            rocketLauncher = new RocketLauncher(context);
            if (launcherParams == null) {
                launcherParams = new WindowManager.LayoutParams();
                launcherParams.x = screenWidth / 2 - RocketLauncher.width / 2;
                launcherParams.y = screenHeight - RocketLauncher.height;
                launcherParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                launcherParams.format = PixelFormat.RGBA_8888;
                launcherParams.gravity = Gravity.LEFT | Gravity.TOP;
                launcherParams.width = RocketLauncher.width;
                launcherParams.height = RocketLauncher.height;
            }
            windowManager.addView(rocketLauncher, launcherParams);
        }
    }

    /**
     * 将火箭发射台从屏幕上移除
     * @param context
     */
    public static void removeLauncher(Context context) {
        if (rocketLauncher != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(rocketLauncher);
            rocketLauncher = null;
        }
    }

    /**
     * 更新火箭发射台显示状态
     */
    public static void updateLauncher() {
        if (rocketLauncher != null) {
            rocketLauncher.updateLauncherStatus(isReadyToLaunch());
        }
    }

    /**
     * 判断小火箭是否准备好发射了
     * @return当火箭被发到发射台上返回true，否则返回false
     */
    public static boolean isReadyToLaunch() {
        if ((smallWindowParams.x > launcherParams.x && smallWindowParams.x
                + smallWindowParams.width < launcherParams.x
                + launcherParams.width)
                && (smallWindowParams.y + smallWindowParams.height > launcherParams.y)) {
            return true;
        }
        return false;
    }





    /**
     * 更新小悬浮窗内存百分比
     * @param context
     */
    public static void updateUsedPercent(Context context) {
        if (susWindowSmallView != null) {
            TextView percentView = (TextView) susWindowSmallView.findViewById(R.id.percent);
            percentView.setText(getUsedPercentValue(context));
        }
    }


    /**
     * 是否有悬浮窗
     * @return
     */
    public static boolean isWindowShowing() {
        return susWindowSmallView != null || susWindowBigView != null;
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     * @param context
     * @return
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * 如果ActivityManager还未创建，则创建一个新的ActivityManager返回。否则返回当前已创建的ActivityManager
     * @param context
     * @return
     */
    private static ActivityManager getActivityManager(Context context) {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return mActivityManager;
    }


    /**
     * 计算已使用内存的百分比，并返回已使用内存百分比，已字符串形式返回
     * @param context
     * @return
     */
    public static String getUsedPercentValue(Context context) {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr,2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
            long availableSize = getAvailableMemory(context) / 1024;
            int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
            return percent + "%";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "悬浮窗";
    }

    /**
     * 获取当前可用内存，返回数据以字节为单位。
     * @param context
     * @return
     */
    public static long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        getActivityManager(context).getMemoryInfo(mi);
        return mi.availMem;
    }
}
