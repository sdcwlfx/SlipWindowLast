package com.example.slipwindow.broadcast;
/**
 * 监听电池变化开启流量管理服务
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.slipwindow.service.FlowManageService;
import com.example.slipwindow.service.FlowWarningListenService;

public class AutoStartImportServiceReceiver extends BroadcastReceiver {
    public AutoStartImportServiceReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        SharedPreferences.Editor editor=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE).edit();
        editor.putString("import","接受到电量变化广播");
        editor.apply();
        Intent intent1=new Intent();
        intent1.setClass(context, FlowManageService.class);
        context.startService(intent1);
        Intent intent2=new Intent();
        intent2.setClass(context, FlowWarningListenService.class);
        context.startService(intent2);
    }
}
