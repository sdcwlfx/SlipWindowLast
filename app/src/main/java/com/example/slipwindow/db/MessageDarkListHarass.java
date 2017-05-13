package com.example.slipwindow.db;

import org.litepal.crud.DataSupport;

/**
 * 短信拦截黑名单
 *
 */

public class MessageDarkListHarass extends DataSupport {
    private String phoneNum;//号码

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
