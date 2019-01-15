package com.example.slipwindow.entity;

import android.content.pm.ApplicationInfo;

/**
 * 存储应用流量信息
 * Created by asus on 2017-05-26.
 */

public class FlowAppUsedSituation {
    private ApplicationInfo applicationInfo;
    private String usedMobile;

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public String getUsedMobile() {
        return usedMobile;
    }

    public void setUsedMobile(String usedMobile) {
        this.usedMobile = usedMobile;
    }
}
