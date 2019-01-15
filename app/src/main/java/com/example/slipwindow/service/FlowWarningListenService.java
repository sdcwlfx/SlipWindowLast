package com.example.slipwindow.service;
/**
 * 监听日月流量使用提醒或断网
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.TrafficStats;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;

import com.example.slipwindow.FlowManageActivity;
import com.example.slipwindow.R;
import com.example.slipwindow.db.MobileUsedRecorder;
import com.example.slipwindow.util.FlowStorageManage;
import com.example.slipwindow.util.TextFormat;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FlowWarningListenService extends Service {
    private Timer timer;//定时检查是否过了一天
    private SharedPreferences pre;
    private SharedPreferences.Editor editor;
    private float usedMonthMobile;
    private float toatalMonthMobile;
    private float monthTotalMobile;
    private int monthWarningMobile;
    private int dayWarningMobile;
    private float usedThisDay;
    private boolean hasNumber;
    public FlowWarningListenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate(){
        super.onCreate();
        pre=getSharedPreferences("phoneModle",MODE_PRIVATE);
    }


    public int onStartCommand(Intent intent,int flags,int startId){
        // 开启定时器，每隔1分钟刷新一次,检查流量使用情况
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new FlowWarningListenService.RefeshTimer(), 0, 1000*60);
        }

        return super.onStartCommand(intent,flags,startId);
    }

    public void onDestroy(){
        super.onDestroy();
        Intent intent=new Intent();
        intent.setClass(this,FlowWarningListenService.class);
        startService(intent);
        Intent intent1=new Intent();
        intent1.setClass(this,FlowManageService.class);
        startService(intent1);
    }

    class RefeshTimer extends TimerTask {
        public void run() {
            hasNumber=pre.getBoolean("hasNumber",false);
            if(hasNumber){//设置了套餐
                editor=pre.edit();
                //以下三变量月更新在FlowManageService中
                boolean dayHasWarning=pre.getBoolean("dayHasWarning",false);//已日提醒过？
                boolean monthHasWarning=pre.getBoolean("monthHasWarning",false);//已月提醒过？
                boolean monthLimitWarning=pre.getBoolean("monthLimitWarning",false);//月限额提醒//
                String monthMore=pre.getString("monthMore","断网");
                toatalMonthMobile=pre.getFloat("totalMonthMobile",-1);//月套餐
                monthTotalMobile=pre.getFloat("monthFlowMobile",-1);//月总流量
                float totalMobile;
                if(monthTotalMobile<0){
                    totalMobile=toatalMonthMobile;
                }else{
                    totalMobile=monthTotalMobile;
                }
                usedMonthMobile=pre.getFloat("usedToatalMonthMobile",-1);
                monthWarningMobile=pre.getInt("monthWarningNumber",-1);//月流量提示额
                dayWarningMobile=pre.getInt("dayWarningMobile",-1);//日流量提示额单位都是MB
                long mobileRxBytes= TrafficStats.getMobileRxBytes();//获取总的接受字节数
                long mobileTxBytes=TrafficStats.getMobileTxBytes();//获取所有发送字节数
                long mobileBytes=mobileRxBytes+mobileTxBytes;
                usedMonthMobile+= TextFormat.formatToMbFromByte(mobileBytes);//本月已使用的总流量
                List<MobileUsedRecorder> mobileUsedRecorders=FlowStorageManage.getThisDayUsedTotalMobile();
                if(mobileUsedRecorders.size()==0){//本天使用的总流量
                    usedThisDay=TextFormat.formatToMbFromByte(mobileBytes);
                }else{
                    usedThisDay=TextFormat.formatToMbFromByte(mobileUsedRecorders.get(0).getMobileUsedFlow()+mobileBytes);
                }
                if(usedThisDay>=dayWarningMobile&&!dayHasWarning){//本月未日提醒过，日提醒通知
                    editor.putBoolean("dayHasWarning",true);
                    editor.apply();
                    Intent intent=new Intent(FlowWarningListenService.this, FlowManageActivity.class);
                    PendingIntent pendingIntent=PendingIntent.getActivity(FlowWarningListenService.this,0,intent,0);
                    Notification notification=new NotificationCompat.Builder(FlowWarningListenService.this)
                            .setContentTitle("日超额提醒")
                            .setContentText("今日流量消耗已达限定值"+TextFormat.formatByte(dayWarningMobile*1024*1024))
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.app1_icon)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.app1_icon))
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .build();
                    startForeground(1,notification);
                }else if(usedMonthMobile>=monthWarningMobile&&!monthHasWarning){//本月未月提醒过，月提醒
                    editor.putBoolean("monthHasWarning",true);
                    editor.apply();
                    Intent intent=new Intent(FlowWarningListenService.this, FlowManageActivity.class);
                    PendingIntent pendingIntent=PendingIntent.getActivity(FlowWarningListenService.this,0,intent,0);
                    Notification notification=new NotificationCompat.Builder(FlowWarningListenService.this)
                            .setContentTitle("月超额提醒")
                            .setContentText("本月流量消耗已达限定值"+TextFormat.formatByte(monthWarningMobile*1024*1024))
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.app1_icon)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.app1_icon))
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .build();
                    startForeground(1,notification);
                }else if(usedMonthMobile>=totalMobile&&!monthLimitWarning){//月限额并且未提醒过
                    editor.putBoolean("monthLimitWarning",true);
                    editor.apply();
                    if(monthMore.equals("断网")){
                        boolean is=FlowStorageManage.getMobileDataState(FlowWarningListenService.this,null);
                        if(is){//已联网//仅支持5.0系统以上版本
                            FlowStorageManage.setMobileData(FlowWarningListenService.this,false);//关网络
                        }
                        new AlertDialog.Builder(FlowWarningListenService.this)
                                .setTitle("提示")
                                .setMessage("数据开关已关闭")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }else if(monthMore.equals("提醒")){
                        Intent intent=new Intent(FlowWarningListenService.this, FlowManageActivity.class);
                        PendingIntent pendingIntent=PendingIntent.getActivity(FlowWarningListenService.this,0,intent,0);
                        Notification notification=new NotificationCompat.Builder(FlowWarningListenService.this)
                                .setContentTitle("月超额提醒")
                                .setContentText("本月流量消耗已达"+TextFormat.formatByte((int)totalMobile*1024*124))
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.app1_icon)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.app1_icon))
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .build();
                        startForeground(1,notification);
                    }


                }

            }
        }
    }


}
