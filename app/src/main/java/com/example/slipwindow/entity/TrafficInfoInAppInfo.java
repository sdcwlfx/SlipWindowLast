package com.example.slipwindow.entity;

import android.content.pm.ApplicationInfo;

/**
 * Created by asus on 2017-05-27.
 */

public class TrafficInfoInAppInfo {
    private int uid;
    private ApplicationInfo applicationInfo;
    private String packageName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }
}
