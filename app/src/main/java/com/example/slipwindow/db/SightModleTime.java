package com.example.slipwindow.db;
/**
 * 情景模式时间段及该时间段的拦截电话及时间段状态是启用还是禁止
 */

import org.litepal.crud.DataSupport;

import java.util.ArrayList;


public class SightModleTime extends DataSupport {
    private int startHourTime;//开始小时
    private int startMinuteTime;//开始分钟
    private int endHourTime;//结束小时
    private int endMinuteTime;//结束分钟
   // private String phoneNum;//拦截电话
    private ArrayList<TimePhone> timePhones=new ArrayList<TimePhone>();//拦截电话
    private String state;//start表示启用还是end表示禁用

    public int getStartHourTime() {
        return startHourTime;
    }

    public void setStartHourTime(int startHourTime) {
        this.startHourTime = startHourTime;
    }

    public int getStartMinuteTime() {
        return startMinuteTime;
    }

    public void setStartMinuteTime(int startMinuteTime) {
        this.startMinuteTime = startMinuteTime;
    }

    public int getEndHourTime() {
        return endHourTime;
    }

    public void setEndHourTime(int endHourTime) {
        this.endHourTime = endHourTime;
    }

    public int getEndMinuteTime() {
        return endMinuteTime;
    }

    public void setEndMinuteTime(int endMinuteTime) {
        this.endMinuteTime = endMinuteTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

   /* public ArrayList<String> getPhoneHarassList() {
        return phoneHarassList;
    }

    public void setPhoneHarassList(ArrayList<String> phoneHarassList) {
        this.phoneHarassList = phoneHarassList;
    }*/

    /*public TimePeople getTimePeople() {
        return timePeople;
    }

    public void setTimePeople(TimePeople timePeople) {
        this.timePeople = timePeople;
    }*/

    public ArrayList<TimePhone> getTimePhones() {
        return timePhones;
    }

    public void setTimePhones(ArrayList<TimePhone> timePhones) {
        this.timePhones = timePhones;
    }
}
