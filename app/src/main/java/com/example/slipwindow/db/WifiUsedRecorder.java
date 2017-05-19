package com.example.slipwindow.db;

import org.litepal.crud.DataSupport;

import java.sql.Date;

/**
 * 总的wifi使用流量，用总流量减去总移动流量,最多包含30天内记录
 * Created by asus on 2017-05-14.
 */

public class WifiUsedRecorder extends DataSupport {
    private long wifiUsedFlow;//总使用的流量：包括wifi
    private String date;//使用日期

    public long getWifiUsedFlow() {
        return wifiUsedFlow;
    }

    public void setWifiUsedFlow(long wifiUsedFlow) {
        this.wifiUsedFlow = wifiUsedFlow;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
