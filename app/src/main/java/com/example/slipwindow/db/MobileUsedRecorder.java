package com.example.slipwindow.db;

import org.litepal.crud.DataSupport;

import java.sql.Date;

/**
 * 总的移动数据使用流量，并且定期清理无用数据
 * Created by asus on 2017-05-14.
 */

public class MobileUsedRecorder extends DataSupport {
    private long mobileUsedFlow;//总使用的流量：包括移动数据
    private String date;//使用日期

    public long getMobileUsedFlow() {
        return mobileUsedFlow;
    }

    public void setMobileUsedFlow(long mobileUsedFlow) {
        this.mobileUsedFlow = mobileUsedFlow;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
