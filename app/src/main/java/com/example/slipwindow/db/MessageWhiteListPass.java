package com.example.slipwindow.db;

import org.litepal.crud.DataSupport;

/**
 * 接受信息白名单
 *
 */

public class MessageWhiteListPass extends DataSupport {
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
