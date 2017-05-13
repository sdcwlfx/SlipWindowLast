package com.example.slipwindow.entity;

/**
 * 短信实例对应数据库非拦截短信
 * Created by asus on 2017-04-14.
 */

public class Message {
    private String address;//短信号码
    private String person;//联系人，陌生人为null
    private long date;//日期
    private int read;//是否阅读，0未读，1已读
    private int type;//1是接收到的，2是已发出
    private String body;//短信具体内容
    private int protocal;//0 SMS_RPOTO短信，1 MMS_PROTO彩信

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
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

    public int getProtocal() {
        return protocal;
    }

    public void setProtocal(int protocal) {
        this.protocal = protocal;
    }
}
