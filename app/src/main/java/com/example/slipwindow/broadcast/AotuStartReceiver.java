package com.example.slipwindow.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.slipwindow.service.FlowManageService;
import com.example.slipwindow.service.FlowWarningListenService;

public class AotuStartReceiver extends BroadcastReceiver {
    public AotuStartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        Intent intent1=new Intent(context, FlowManageService.class);
        intent1.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.startService(intent1);
        Intent intent2=new Intent();
        intent2.setClass(context, FlowWarningListenService.class);
        context.startService(intent2);
        SharedPreferences.Editor editor=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE).edit();
        editor.putString("start","接受到开机广播");
        editor.apply();
       // Toast.makeText(context,"开机自启",Toast.LENGTH_SHORT).show();
    }
}
