package com.example.slipwindow.db;

import android.content.pm.ApplicationInfo;

import org.litepal.crud.DataSupport;

import java.sql.Date;

/**
 * 应用wifi使用流量
 * Created by asus on 2017-05-14.
 */

public class WifiUsedSelectedAppRecorder extends DataSupport {
    private ApplicationInfo applicationInfo;//应用信息
    private long wifiUsed;//wifi用量
    private String date;//日期

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public long getWifiUsed() {
        return wifiUsed;
    }

    public void setWifiUsed(long wifiUsed) {
        this.wifiUsed = wifiUsed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
