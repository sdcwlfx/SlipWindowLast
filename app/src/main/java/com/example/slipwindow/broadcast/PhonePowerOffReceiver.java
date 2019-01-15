package com.example.slipwindow.broadcast;
/**
 * 关机广播接收器
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.slipwindow.util.FlowStorageManage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PhonePowerOffReceiver extends BroadcastReceiver {
    long oldTotalBytes=0;
    long oldMobileBytes=0;
    String TODAY_USED_MOBILE="com.exmple.slipwindow.TODAY_USED_MOBILE";
    public PhonePowerOffReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
       // throw new UnsupportedOperationException("Not yet implemented");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String nowDate=df.format(new Date());
        SharedPreferences pre=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();//从新计前天流量
        if(intent.getAction().equals(TODAY_USED_MOBILE)){
            editor.putString("todayUsedMobile","获取了更新广播");
            abortBroadcast();
            editor.putString("phoneClose","接受了关机广播");
            editor.apply();
            String oldDate=pre.getString("date","");
            if(oldDate.equals(nowDate)){//同一天
                //SharedPreferences.Editor editor=pre.edit();
                editor.putLong("totalBytes",0);//总流量
                editor.putLong("mobileBytes",0);//总移动数据
                //editor.apply();
            }else{//不是同一天
                oldTotalBytes=pre.getLong("totalBytes",0);
                oldMobileBytes=pre.getLong("mobileBytes",0);
            }
            FlowStorageManage.addAllFlowUsed(oldTotalBytes);//总流量更新
            FlowStorageManage.addMobileUsed(oldMobileBytes);//总移动数据更新
            FlowStorageManage.addMobileUsedSelectedApp(context);//更新各App流量使用情况
            FlowStorageManage.setAppTodayMobilePowerOff(context);//设置今日流量
        }else{
            editor.putString("phoneClose","接受了关机广播");
            editor.apply();
            String oldDate=pre.getString("date","");
            if(oldDate.equals(nowDate)){//同一天
                //SharedPreferences.Editor editor=pre.edit();
                editor.putLong("totalBytes",0);//总流量
                editor.putLong("mobileBytes",0);//总移动数据
                //editor.apply();
            }else{//不是同一天
                oldTotalBytes=pre.getLong("totalBytes",0);
                oldMobileBytes=pre.getLong("mobileBytes",0);
            }
            FlowStorageManage.addAllFlowUsed(oldTotalBytes);//总流量更新
            FlowStorageManage.addMobileUsed(oldMobileBytes);//总移动数据更新
            FlowStorageManage.addMobileUsedSelectedApp(context);//更新各App流量使用情况
            editor.putLong("totalBytes",0);//总流量
            editor.putLong("mobileBytes",0);//总移动数据
            // editor.putBoolean("flowManage",true);
            editor.apply();
            FlowStorageManage.setAppMobilePowerOff(context);
        }

      /*  editor.putString("phoneClose","接受了关机广播");
        editor.apply();
        String oldDate=pre.getString("date","");
        if(oldDate.equals(nowDate)){//同一天
            //SharedPreferences.Editor editor=pre.edit();
            editor.putLong("totalBytes",0);//总流量
            editor.putLong("mobileBytes",0);//总移动数据
            //editor.apply();
        }else{//不是同一天
            oldTotalBytes=pre.getLong("totalBytes",0);
            oldMobileBytes=pre.getLong("mobileBytes",0);
        }
        FlowStorageManage.addAllFlowUsed(oldTotalBytes);//总流量更新
        FlowStorageManage.addMobileUsed(oldMobileBytes);//总移动数据更新
        FlowStorageManage.addMobileUsedSelectedApp(context);//更新各App流量使用情况
        editor.putLong("totalBytes",0);//总流量
        editor.putLong("mobileBytes",0);//总移动数据
        // editor.putBoolean("flowManage",true);
        editor.apply();
        FlowStorageManage.setAppMobilePowerOff(context);*/



    }
}
