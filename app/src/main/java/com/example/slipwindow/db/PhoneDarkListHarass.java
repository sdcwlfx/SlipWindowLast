package com.example.slipwindow.db;

/**
 * .电话拦截黑名单
 */
import org.litepal.crud.DataSupport;
public class PhoneDarkListHarass extends DataSupport {
    private String phoneNum;//号码

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }


}
