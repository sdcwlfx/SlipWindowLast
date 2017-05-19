package com.example.slipwindow.db;

import org.litepal.crud.DataSupport;

import java.sql.Date;

/**
 *总流量使用记录表，定期清理过期无记录
 * Created by asus on 2017-05-14.
 */

public class FlowUsedRecorder extends DataSupport {
    private long usedFlow;//总使用的流量：包括数据和wifi
   // private Date date;//使用日期
    private String date;


    public long getUsedFlow() {
        return usedFlow;
    }

    public void setUsedFlow(long usedFlow) {
        this.usedFlow = usedFlow;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
