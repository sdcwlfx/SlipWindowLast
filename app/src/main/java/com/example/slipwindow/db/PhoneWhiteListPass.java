package com.example.slipwindow.db;

import org.litepal.crud.DataSupport;

/**
 * .电话放行白名单()
 */

public class PhoneWhiteListPass extends DataSupport {
    private String phoneNum;//号码
   // private boolean phonePass;//电话放行
   // private boolean messagePass;//短信放行

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

  /*  public boolean isPhonePass() {
        return phonePass;
    }

    public void setPhonePass(boolean phonePass) {
        this.phonePass = phonePass;
    }

    public boolean isMessagePass() {
        return messagePass;
    }

    public void setMessagePass(boolean messagePass) {
        this.messagePass = messagePass;
    }*/
}
