package com.example.slipwindow.service;
/**
 *若一天结束，则发送自定义广播，在相应广播接收器中存储今日所用总流量和总移动数据
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.TrafficStats;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.slipwindow.FlowManageActivity;
import com.example.slipwindow.R;
import com.example.slipwindow.util.FlowStorageManage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FlowManageService extends Service {
    private Timer timer;//定时检查是否过了一天
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
    public FlowManageService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate(){
        super.onCreate();
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
                .setSmallIcon(R.mipmap.app1_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.app1_icon))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        startForeground(1,notification);
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        // 开启定时器，每隔1分钟刷新一次
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefeshTimer(), 0, 1000*59);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy(){//
        super.onDestroy();
        Intent intent=new Intent();
        intent.setClass(this,FlowManageService.class);
        startService(intent);

        Intent intent1=new Intent();//
        intent1.setClass(this,FlowWarningListenService.class);///
        startService(intent1);//
    }

    class RefeshTimer extends TimerTask{
        public void run(){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
            Date nowDate=new Date();//24小时制
            String date=df.format(nowDate);
            String time=simpleDateFormat.format(nowDate);
            String date1=pre.getString("nextMonth","");
            if(date1.equals(date)){//新的一月更新提醒标志，设为未提醒
                SharedPreferences.Editor editor=pre.edit();
                Calendar c=Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.MONTH,1);
                Date result=c.getTime();
                String date2=df.format(result);
                editor.putString("nextMonth",date2);//将更新时间设为下一月
                editor.putBoolean("dayHasWarning",false);//更新日提醒，本月未提醒过
                editor.putBoolean("monthHasWarning",false);//更新月提醒，本月未提醒过
                editor.putBoolean("monthLimitWarning",false);//更新月限额，本月未提醒过
                editor.apply();
            }
           // if(time.equals("23:58")){//过了一天，发送自定义广播存储各应用流量使用
            if(time.equals("23:28")){
                SharedPreferences pre=getSharedPreferences("phoneModle",MODE_PRIVATE);
                SharedPreferences.Editor editor=pre.edit();
               // if(pre.getBoolean("flowManage",true)){
                 //   editor.putBoolean("flowManage",false);
                    //editor.apply();
                long totalRxBytes= TrafficStats.getTotalRxBytes();//获取总的接受字节数
                long totalTxBytes=TrafficStats.getTotalTxBytes();//获取所有发送字节数
                long totalBytes=totalRxBytes+totalTxBytes;
                long mobileRxBytes=TrafficStats.getMobileRxBytes();//获取总的接受字节数
                long mobileTxBytes=TrafficStats.getMobileTxBytes();//获取所有发送字节数
                long mobileBytes=mobileRxBytes+mobileTxBytes;
                long oldTotalBytes=pre.getLong("totalBytes",0);//昨天使用量
                long oldMobileBytes=pre.getLong("mobileBytes",0);//昨天使用移动数据量
                editor.putLong("oldTotalBytes",oldTotalBytes);//昨天使用量
                editor.putLong("oldMobileBytes",oldMobileBytes);//昨天使用移动数据量
                editor.putString("date",date);
                editor.putLong("totalBytes",totalBytes);//总流量
                editor.putLong("mobileBytes",mobileBytes);//总移动数据
                editor.apply();
                FlowStorageManage.setAllAppMobileUsed(FlowManageService.this,date);
              //  }
                Intent intent=new Intent("com.example.slipwindow.MY_FLOWMANAGE_BROADCAST");//发送有序广播
                sendOrderedBroadcast(intent,null);
            }

        }
    }
}
