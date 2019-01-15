package com.example.slipwindow.db;

import org.litepal.crud.DataSupport;

/**
 * Created by asus on 2017-06-04.
 */

public class Permission extends DataSupport {
    private String permission;//英文权限
    private String name;//中文权限

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
