package com.example.slipwindow.db;

/**
 * Created by asus on 2017-04-04.电话短信黑名单
 */
import org.litepal.crud.DataSupport;
public class DarkListHarass extends DataSupport {
    private int phoneNum;//号码
    private boolean phoneHarass;//true拦截电话
    private boolean messageHarass;//拦截短信true

    public int getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(int phoneNum) {
        this.phoneNum = phoneNum;
    }

    public boolean isPhoneHarass() {
        return phoneHarass;
    }

    public void setPhoneHarass(boolean phoneHarass) {
        this.phoneHarass = phoneHarass;
    }

    public boolean isMessageHarass() {
        return messageHarass;
    }

    public void setMessageHarass(boolean messageHarass) {
        this.messageHarass = messageHarass;
    }
}
