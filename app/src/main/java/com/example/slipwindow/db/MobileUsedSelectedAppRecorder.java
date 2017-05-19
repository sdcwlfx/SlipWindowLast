package com.example.slipwindow.db;

import android.content.pm.ApplicationInfo;

import org.litepal.crud.DataSupport;

import java.sql.Date;

/**
 * 各应用数据使用记录
 * Created by asus on 2017-05-14.
 */

public class MobileUsedSelectedAppRecorder extends DataSupport {
    private String  packageName;//应用信息
    private long mobileUsed;//使用的量
    private String date;//日期

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getMobileUsed() {
        return mobileUsed;
    }

    public void setMobileUsed(long mobileUsed) {
        this.mobileUsed = mobileUsed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
