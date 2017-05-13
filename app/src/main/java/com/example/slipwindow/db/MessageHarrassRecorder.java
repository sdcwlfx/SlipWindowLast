package com.example.slipwindow.db;

import org.litepal.crud.DataSupport;

/**
 * 拦截短信记录表
 * Created by asus on 2017-04-14.
 */

public class MessageHarrassRecorder extends DataSupport{
    private String address;//短信号码
    private long date;//日期
    private int read;//是否阅读，0未读，1已读
    private int type;//1是接收到的，2是已发出
    private String body;//短信具体内容
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
