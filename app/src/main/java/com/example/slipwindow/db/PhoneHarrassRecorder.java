package com.example.slipwindow.db;

import org.litepal.crud.DataSupport;

/**
 * 拦截电话记录表
 *
 */

public class PhoneHarrassRecorder extends DataSupport {
    private String address;//地址
    private String date;//拦截时间
    private String name;//姓名

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
