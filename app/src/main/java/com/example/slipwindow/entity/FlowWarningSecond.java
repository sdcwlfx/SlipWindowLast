package com.example.slipwindow.entity;

/**
 * Created by asus on 2017-05-23.
 */

public class FlowWarningSecond {
    private String text1;
    private String text2;
    private int maxMobile;//进度条最大值
    private int seekBarNum;//进度条值
    private String percent;//百分比

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public int getMaxMobile() {
        return maxMobile;
    }

    public void setMaxMobile(int maxMobile) {
        this.maxMobile = maxMobile;
    }

    public int getSeekBarNum() {
        return seekBarNum;
    }

    public void setSeekBarNum(int seekBarNum) {
        this.seekBarNum = seekBarNum;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }
}
