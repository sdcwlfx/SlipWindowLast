package com.example.slipwindow.entity;
/**
 * 任务管理进程实例
 */

import android.graphics.drawable.Drawable;

/**
 * Created by asus on 2017-04-26.
 */

public class TaskInfo {

    private Drawable taskIcon;//图标
    private String taskName;//名字
    private long taskMemeroy;//占用内存
    private String packageName;//包名
    private int id;//进程id

    public Drawable getTaskIcon() {
        return taskIcon;
    }

    public void setTaskIcon(Drawable taskIcon) {
        this.taskIcon = taskIcon;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public long getTaskMemeroy() {
        return taskMemeroy;
    }

    public void setTaskMemeroy(long taskMemeroy) {
        this.taskMemeroy = taskMemeroy;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
