package com.example.slipwindow.entity;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * app信息
 * Created by asus on 2017-04-06.
 */

public class AppInfo {
    private String appNmae;//APP名字
    private String appPackageName;//app包名
    private Intent intent;//启动APP
    private Drawable icon;//程序图标

   /* final void setActivity(ComponentName className,int launchFlags){
        intent=new Intent();
    }*/

    public String getAppNmae() {
        return appNmae;
    }

    public void setAppNmae(String appNmae) {
        this.appNmae = appNmae;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }


}
