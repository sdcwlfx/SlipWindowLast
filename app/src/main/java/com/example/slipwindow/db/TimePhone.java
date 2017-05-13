package com.example.slipwindow.db;
/**
 * 关联SigghtModleTime表，多对一，存储拦截电话对象
 */

import org.litepal.crud.DataSupport;


public class TimePhone extends DataSupport {
    private SightModleTime sightModleTime;//多对一关系
    private String phoneNum;//电话

    public SightModleTime getSightModleTime() {
        return sightModleTime;
    }

    public void setSightModleTime(SightModleTime sightModleTime) {
        this.sightModleTime = sightModleTime;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
