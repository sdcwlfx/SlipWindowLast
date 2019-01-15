package com.example.slipwindow.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.slipwindow.FlowManageActivity;
import com.example.slipwindow.R;

public class InformService extends Service {
    private float totalMonthMobile;
    private float usedThisMonth;
    private float monthUsedMobile;
    private Intent intent;
    private String show;
    private String not="还未设置流量套餐";
    private String used="已用：";
    private String last="剩余：";
    private SharedPreferences pre;
    private Boolean hasNumber;
    public InformService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        pre=getSharedPreferences("phoneModle", Context.MODE_PRIVATE);
        hasNumber=pre.getBoolean("hasNumber",false);
        if(hasNumber){//设置了套餐
            totalMonthMobile = pre.getFloat("totalMonthMobile", 0);//月流量套餐，单位为MB
            usedThisMonth = pre.getFloat("usedToatalMonthMobile", 0);
            monthUsedMobile=pre.getFloat("monthFlowMobile",-1);
            used+=String.valueOf(usedThisMonth);
            if(totalMonthMobile>=usedThisMonth){
                last+=String.valueOf(totalMonthMobile-usedThisMonth);
            }else{
                last="超额："+String.valueOf(usedThisMonth-totalMonthMobile);
            }
            show=used+last;
        }else{
            show=not;
        }
        intent=new Intent(this, FlowManageActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);
        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle(show)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        startForeground(1,notification);
        return super.onStartCommand(intent, flags, startId);
    }
}
