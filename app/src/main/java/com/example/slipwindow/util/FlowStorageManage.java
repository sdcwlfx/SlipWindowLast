package com.example.slipwindow.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.TrafficStats;

import com.example.slipwindow.db.FlowUsedRecorder;
import com.example.slipwindow.db.MobileUsedRecorder;
import com.example.slipwindow.db.MobileUsedSelectedAppRecorder;
import com.example.slipwindow.db.WifiUsedRecorder;
import com.example.slipwindow.entity.FlowAppUsedSituation;
import com.example.slipwindow.entity.TrafficInfo;
import com.example.slipwindow.entity.TrafficInfoInAppInfo;

import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 存储各应用流量使用状况
 * Created by asus on 2017-05-13.
 */

public class FlowStorageManage {


    /**
     * 返回年月日的时间
     * @param date
     * @return
     */
    public static  Date getDate(Date date){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String date1=df.format(date);
        Date date2=null;
        try{
            date2=df.parse(date1);
            return date2;
        }catch (ParseException e){

        }
        return date2;
    }

    /**
     * 存储总的流量，若已存在同一天则更新该天数据，存到数据库，当关机或过了一天时调用
     * oldTotalBytes :
     */
    public static void addAllFlowUsed(long oldTotalBytes){
        long totalRxBytes=TrafficStats.getTotalRxBytes();//获取总的接受字节数
        long totalTxBytes=TrafficStats.getTotalTxBytes();//获取所有发送字节数
        Date date=getDate(new Date());//年月日
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String date1=df.format(date);
        java.sql.Date date2= java.sql.Date.valueOf(date1);
        if(totalRxBytes!=-1&&totalTxBytes!=-1){
            List<FlowUsedRecorder> flowUsedRecorders= DataSupport.where("date = ?",date1).find(FlowUsedRecorder.class);
            if(flowUsedRecorders.size()==0){//无已有日期记录
                FlowUsedRecorder flowUsedRecorder=new FlowUsedRecorder();
                flowUsedRecorder.setUsedFlow(totalRxBytes+totalTxBytes-oldTotalBytes);//过了一天减去前天记录oldTotalBytes
                flowUsedRecorder.setDate(date1);//日子几号
                flowUsedRecorder.save();//提交记录
            }else{
                FlowUsedRecorder flowUsedRecorder=flowUsedRecorders.get(0);
                long old=flowUsedRecorder.getUsedFlow();
                long newBytes=old+totalRxBytes+totalTxBytes-oldTotalBytes;
                flowUsedRecorder.setUsedFlow(newBytes);
                flowUsedRecorder.save();//更新
            }
        }
    }

    /**
     * 存储已用的总的移动数据量,当关机或过了一天时调用
     */
    public static void addMobileUsed(long oldMobileBytes){
        long mobileRxBytes=TrafficStats.getMobileRxBytes();//获取总的接受字节数
        long mobileTxBytes=TrafficStats.getMobileTxBytes();//获取所有发送字节数
        Date date=getDate(new Date());//年月日
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String date1=df.format(date);
        java.sql.Date date2= java.sql.Date.valueOf(date1);
        if(mobileRxBytes!=-1&&mobileTxBytes!=-1){
            List<MobileUsedRecorder> mobileUsedRecorders= DataSupport.where("date = ?",date1).find(MobileUsedRecorder.class);
            if(mobileUsedRecorders.size()==0){
                MobileUsedRecorder mobileUsedRecorder=new MobileUsedRecorder();
                mobileUsedRecorder.setMobileUsedFlow(mobileRxBytes+mobileTxBytes-oldMobileBytes);
                mobileUsedRecorder.setDate(date1);
                mobileUsedRecorder.save();
            }else{
                MobileUsedRecorder mobileUsedRecorder=mobileUsedRecorders.get(0);
                long old=mobileUsedRecorder.getMobileUsedFlow();
                long newBytes=old+mobileRxBytes+mobileTxBytes-oldMobileBytes;
                mobileUsedRecorder.setMobileUsedFlow(newBytes);
                mobileUsedRecorder.save();//更新
            }
        }
    }

    /**
     * 存储已用的总的wifi量,当关机或过了一天时调用(暂时不用)
     */
    public static void addWifiUsed(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        long totalRxBytes=TrafficStats.getTotalRxBytes();//获取总的接受字节数
        long totalTxBytes=TrafficStats.getTotalTxBytes();//获取所有发送字节数
        long mobileRxBytes=TrafficStats.getMobileRxBytes();//获取总的接受字节数
        long mobileTxBytes=TrafficStats.getMobileTxBytes();//获取所有发送字节数
        WifiUsedRecorder wifiUsedRecorder=new WifiUsedRecorder();
        long wifiUsed=totalRxBytes+totalTxBytes-mobileRxBytes-mobileTxBytes;
        wifiUsedRecorder.setWifiUsedFlow(wifiUsed);
        wifiUsedRecorder.setDate(df.format(new Date().getTime()));
        wifiUsedRecorder.save();
    }

    /**
     * 存储指定APP的流量使用量，当关机时调用,并对30天外的记录清理掉
     */
   /* public static void addMobileUsedSelectedApp(Context context){
        PackageManager packageManager=context.getPackageManager();
        List<ApplicationInfo> applicationInfos=packageManager.getInstalledApplications(0);
        Date date=getDate(new Date());//年月日
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String date1=df.format(date);
        java.sql.Date date2= java.sql.Date.valueOf(date1);
        for(ApplicationInfo applicationInfo:applicationInfos){
            int uid=applicationInfo.uid;//应用uid
            String packageName=applicationInfo.packageName;
            long used=getSelectedPackageUsed(context,packageName);//今天前用过的流量
            long rx=TrafficStats.getUidRxBytes(uid);//下载的流量
            long tx=TrafficStats.getUidTxBytes(uid);//上传的流量
            if(rx!=-1&&tx!=-1){
                List<MobileUsedSelectedAppRecorder> mobileUsedSelectedAppRecorders= DataSupport.where("date = ? and packageName=?",date1,packageName).find(MobileUsedSelectedAppRecorder.class);
                if(mobileUsedSelectedAppRecorders.size()==0){
                    MobileUsedSelectedAppRecorder mobileUsedSelectedAppRecorder=new MobileUsedSelectedAppRecorder();
                    mobileUsedSelectedAppRecorder.setDate(date1);//日期
                    mobileUsedSelectedAppRecorder.setPackageName(packageName);//应用信息
                    mobileUsedSelectedAppRecorder.setMobileUsed(rx+tx-used);//流量
                    mobileUsedSelectedAppRecorder.save();//保存到数据库
                }else{
                    MobileUsedSelectedAppRecorder mobileUsedSelectedAppRecorder=mobileUsedSelectedAppRecorders.get(0);
                    long old=mobileUsedSelectedAppRecorder.getMobileUsed();
                    long newMobileUsed=old+rx+tx-used;
                    mobileUsedSelectedAppRecorder.setMobileUsed(newMobileUsed);
                    mobileUsedSelectedAppRecorder.save();
                }

            }
        }
    }*/

    /**
     * 存储据网络访问权限的应用App
     * @param context
     */
    public static void addMobileUsedSelectedApp(Context context){
        SharedPreferences pre=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE);
        PackageManager packageManager=context.getPackageManager();
        List<TrafficInfo> trafficInfos=new ArrayList<TrafficInfo>();
        List<PackageInfo> packageInfos=packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for(PackageInfo packageInfo:packageInfos){
            String[] permissions=packageInfo.requestedPermissions;
            if(permissions!=null&&permissions.length>0){
                for(String permission:permissions){
                    if("android.permission.INTERNET".equals(permission)){
                        TrafficInfo trafficInfo=new TrafficInfo();
                        trafficInfo.setPackageName(packageInfo.packageName);
                        trafficInfo.setUid(packageInfo.applicationInfo.uid);
                        trafficInfos.add(trafficInfo);
                    }
                }
            }
        }
      //  List<ApplicationInfo> applicationInfos=packageManager.getInstalledApplications(0);
        Date date=getDate(new Date());//年月日
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String date1=df.format(date);
        java.sql.Date date2= java.sql.Date.valueOf(date1);
      //  for(ApplicationInfo applicationInfo:applicationInfos){
        for(TrafficInfo trafficInfo:trafficInfos){
          //  int uid=applicationInfo.uid;//应用uid
           // String packageName=applicationInfo.packageName;
            int uid=trafficInfo.getUid();
            String packageName=trafficInfo.getPackageName();
            long used=getSelectedPackageUsed(context,packageName);//今天前用过的流量、、//出错

            long today=pre.getLong("today"+packageName,0);//今日存储的用过的流量

           // long rx=TrafficStats.getUidRxBytes(uid);//下载的流量
           // long tx=TrafficStats.getUidTxBytes(uid);//上传的流量
            long used1=getTotalBytesManual(uid);
           // if(rx!=-1&&tx!=-1){
                List<MobileUsedSelectedAppRecorder> mobileUsedSelectedAppRecorders= DataSupport.where("date = ? and packageName=?",date1,packageName).find(MobileUsedSelectedAppRecorder.class);
                if(mobileUsedSelectedAppRecorders.size()==0){
                    MobileUsedSelectedAppRecorder mobileUsedSelectedAppRecorder=new MobileUsedSelectedAppRecorder();
                    mobileUsedSelectedAppRecorder.setDate(date1);//日期
                    mobileUsedSelectedAppRecorder.setPackageName(packageName);//应用信息
                    //mobileUsedSelectedAppRecorder.setMobileUsed(rx+tx-used);//流量
                    mobileUsedSelectedAppRecorder.setMobileUsed(used1-used-today);
                    mobileUsedSelectedAppRecorder.save();//保存到数据库
                }else{
                    MobileUsedSelectedAppRecorder mobileUsedSelectedAppRecorder=mobileUsedSelectedAppRecorders.get(0);
                    long old=mobileUsedSelectedAppRecorder.getMobileUsed();
                   // long newMobileUsed=old+rx+tx-used;
                    long newMobileUsed=old+used1-used-today;
                    mobileUsedSelectedAppRecorder.setMobileUsed(newMobileUsed);
                    mobileUsedSelectedAppRecorder.save();
                }

           // }
        }

    }


    /**
     * 根据UID直接从文件中获取
     * @param localUid
     * @return
     */
    public static Long getTotalBytesManual(int localUid) {
        File dir = new File("/proc/uid_stat/");
        String[] children = dir.list();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < children.length; i++) {
            stringBuffer.append(children[i]);
            stringBuffer.append("   ");
        }
        if (!Arrays.asList(children).contains(String.valueOf(localUid))) {
            return 0L;
        }
        File uidFileDir = new File("/proc/uid_stat/" + String.valueOf(localUid));
        File uidActualFileReceived = new File(uidFileDir,"tcp_rcv");
        File uidActualFileSent = new File(uidFileDir,"tcp_snd");
        String textReceived = "0";
        String textSent = "0";
        try {
            BufferedReader brReceived = new BufferedReader(new FileReader(uidActualFileReceived));
            BufferedReader brSent = new BufferedReader(new FileReader(uidActualFileSent));
            String receivedLine;
            String sentLine;

            if ((receivedLine = brReceived.readLine()) != null) {
                textReceived = receivedLine;
            }
            if ((sentLine = brSent.readLine()) != null) {
                textSent = sentLine;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Long.valueOf(textReceived).longValue() + Long.valueOf(textSent).longValue();
    }


    /**
     * 存储指定APP的流量使用量，过了一天时调用,并对30天外的记录清理掉
     */
    public static void addMobileUsedSelectedAppAfterDay(Context context){
        PackageManager packageManager=context.getPackageManager();
       // List<ApplicationInfo> applicationInfos=packageManager.getInstalledApplications(0);
        List<TrafficInfo> trafficInfos=new ArrayList<TrafficInfo>();
        List<PackageInfo> packageInfos=packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for(PackageInfo packageInfo:packageInfos){
            String[] permissions=packageInfo.requestedPermissions;
            if(permissions!=null&&permissions.length>0){
                for(String permission:permissions){
                    if("android.permission.INTERNET".equals(permission)){
                        TrafficInfo trafficInfo=new TrafficInfo();
                        trafficInfo.setPackageName(packageInfo.packageName);
                        trafficInfo.setUid(packageInfo.applicationInfo.uid);
                        trafficInfos.add(trafficInfo);
                    }
                }
            }
        }
        Date date=getDate(new Date());//年月日
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String date1=df.format(date);
      //  for(ApplicationInfo applicationInfo:applicationInfos){
        for(TrafficInfo trafficInfo:trafficInfos){
            //int uid=applicationInfo.uid;//应用uid
            //String packageName=applicationInfo.packageName;
            int uid=trafficInfo.getUid();
            String packageName=trafficInfo.getPackageName();
            String name="old"+packageName;
            long used=getSelectedPackageUsed(context,name);//今天前用过的流量
           // long rx=TrafficStats.getUidRxBytes(uid);//下载的流量
           // long tx=TrafficStats.getUidTxBytes(uid);//上传的流量
            long used1=getTotalBytesManual(uid);
           // if(rx!=-1&&tx!=-1){
                List<MobileUsedSelectedAppRecorder> mobileUsedSelectedAppRecorders= DataSupport.where("date=? and packageName=?",date1,packageName).find(MobileUsedSelectedAppRecorder.class);
                if(mobileUsedSelectedAppRecorders.size()==0){
                    MobileUsedSelectedAppRecorder mobileUsedSelectedAppRecorder=new MobileUsedSelectedAppRecorder();
                    mobileUsedSelectedAppRecorder.setDate(date1);//日期
                    mobileUsedSelectedAppRecorder.setPackageName(packageName);//应用信息
                 //   mobileUsedSelectedAppRecorder.setMobileUsed(rx+tx-used);//流量
                    mobileUsedSelectedAppRecorder.setMobileUsed(used1-used);
                    mobileUsedSelectedAppRecorder.save();//保存到数据库
                }else{
                    MobileUsedSelectedAppRecorder mobileUsedSelectedAppRecorder=mobileUsedSelectedAppRecorders.get(0);
                    long old=mobileUsedSelectedAppRecorder.getMobileUsed();
                  //  long newMobileUsed=old+rx+tx-used;
                    long newMobileUsed=old+used1-used;
                    mobileUsedSelectedAppRecorder.setMobileUsed(newMobileUsed);
                    mobileUsedSelectedAppRecorder.save();
                }

           // }
        }
    }


    /**
     * 获取今日应用流量使用状况降序获取，包含应用信息和今日流量使用量
     * @returne
     */
    public static ArrayList<FlowAppUsedSituation> getTodyUsedMobile(Context context){
        PackageManager packageManager=context.getPackageManager();
        ArrayList<FlowAppUsedSituation> flowAppUsedSituations=new ArrayList<FlowAppUsedSituation>();
        Date date=getDate(new Date());//年月日
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String date1=df.format(date);
        List<MobileUsedSelectedAppRecorder> mobileUsedSelectedAppRecorders= DataSupport.where("date=?",date1).order("mobileUsed desc").find(MobileUsedSelectedAppRecorder.class);//今日流量降序排行
        for(MobileUsedSelectedAppRecorder mobileUsedSelectedAppRecorder:mobileUsedSelectedAppRecorders){
            if(mobileUsedSelectedAppRecorder.getPackageName()!=null&&!mobileUsedSelectedAppRecorder.getPackageName().equals("")&&mobileUsedSelectedAppRecorder.getMobileUsed()>0){
                try{
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(mobileUsedSelectedAppRecorder.getPackageName(), 0);//依据进程名字获取应用信息
                    FlowAppUsedSituation flowAppUsedSituation=new FlowAppUsedSituation();
                    flowAppUsedSituation.setApplicationInfo(applicationInfo);
                    flowAppUsedSituation.setUsedMobile(TextFormat.formatByte(mobileUsedSelectedAppRecorder.getMobileUsed()));//使用量
                    flowAppUsedSituations.add(flowAppUsedSituation);
                }catch (PackageManager.NameNotFoundException e){

                }

            }
        }
        return flowAppUsedSituations;
    }


    /**
     * 获取所有应用最近30天流量使用情况
     * @param context
     * @return
     */
    public static ArrayList<FlowAppUsedSituation> getLastMonth(Context context){
        PackageManager packageManager=context.getPackageManager();
        ArrayList<FlowAppUsedSituation> flowAppUsedSituations=new ArrayList<FlowAppUsedSituation>();
     //   List<ApplicationInfo> applicationInfos=packageManager.getInstalledApplications(0);
        List<TrafficInfoInAppInfo> trafficInfoInAppInfos=new ArrayList<TrafficInfoInAppInfo>();
        List<PackageInfo> packageInfos=packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for(PackageInfo packageInfo:packageInfos){
            String[] permissions=packageInfo.requestedPermissions;
            if(permissions!=null&&permissions.length>0){
                for(String permission:permissions){
                    if("android.permission.INTERNET".equals(permission)){
                        TrafficInfoInAppInfo trafficInfoInAppInfo=new TrafficInfoInAppInfo();
                        trafficInfoInAppInfo.setApplicationInfo(packageInfo.applicationInfo);
                        trafficInfoInAppInfo.setPackageName(packageInfo.packageName);
                        trafficInfoInAppInfo.setUid(packageInfo.applicationInfo.uid);
                        trafficInfoInAppInfos.add(trafficInfoInAppInfo);
                    }
                }
            }
        }
     //   for(ApplicationInfo applicationInfo:applicationInfos) {
        for(TrafficInfoInAppInfo trafficInfoInAppInfo:trafficInfoInAppInfos){
           // int uid = applicationInfo.uid;//应用uid
           // String packageName = applicationInfo.packageName;
            int uid=trafficInfoInAppInfo.getUid();
            String packageName=trafficInfoInAppInfo.getPackageName();
            List<MobileUsedSelectedAppRecorder> mobileUsedSelectedAppRecorders = DataSupport.where("packageName=?", packageName).order("date desc").find(MobileUsedSelectedAppRecorder.class);
            if (mobileUsedSelectedAppRecorders.size() > 0) {
                if (mobileUsedSelectedAppRecorders.size() > 30) {//删除30天外的记录
                    for (int i = 30; i < mobileUsedSelectedAppRecorders.size(); i++) {
                        MobileUsedSelectedAppRecorder mobileUsedSelectedAppRecorder = mobileUsedSelectedAppRecorders.get(i);
                        mobileUsedSelectedAppRecorder.delete();
                        mobileUsedSelectedAppRecorders.remove(i);
                        i--;
                    }
                }
                long usedThisApp = 0;//App最近
                for (int i = 0; i < mobileUsedSelectedAppRecorders.size(); i++) {
                    usedThisApp += mobileUsedSelectedAppRecorders.get(0).getMobileUsed();
                }
                if (usedThisApp > 0) {
                    FlowAppUsedSituation flowAppUsedSituation = new FlowAppUsedSituation();
                    flowAppUsedSituation.setUsedMobile(TextFormat.formatByte(usedThisApp));
                 //   flowAppUsedSituation.setApplicationInfo(applicationInfo);
                    flowAppUsedSituation.setApplicationInfo(trafficInfoInAppInfo.getApplicationInfo());
                    flowAppUsedSituations.add(flowAppUsedSituation);
                }
            }
        }
        return flowAppUsedSituations;

    }





    /**
     * 设置所有应用数据使用
     * @param context
     */
    public static void setAllAppMobileUsed(Context context,String date1){
        PackageManager packageManager=context.getPackageManager();
      //  List<ApplicationInfo> applicationInfos=packageManager.getInstalledApplications(0);
        List<TrafficInfo> trafficInfos=new ArrayList<TrafficInfo>();
        List<PackageInfo> packageInfos=packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for(PackageInfo packageInfo:packageInfos){
            String[] permissions=packageInfo.requestedPermissions;
            if(permissions!=null&&permissions.length>0){
                for(String permission:permissions){
                    if("android.permission.INTERNET".equals(permission)){
                        TrafficInfo trafficInfo=new TrafficInfo();
                        trafficInfo.setPackageName(packageInfo.packageName);
                        trafficInfo.setUid(packageInfo.applicationInfo.uid);
                        trafficInfos.add(trafficInfo);
                    }
                }
            }
        }
        Date date=getDate(new Date());//年月日
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
      //  String date1=df.format(date);
        SharedPreferences pre=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();
      //  for(ApplicationInfo applicationInfo:applicationInfos){
        for(TrafficInfo trafficInfo:trafficInfos){
           // int uid=applicationInfo.uid;//应用uid
           // String packageName=applicationInfo.packageName;
                int uid=trafficInfo.getUid();
                String packageName=trafficInfo.getPackageName();
           // long rx=TrafficStats.getUidRxBytes(uid);//下载的流量
           // long tx=TrafficStats.getUidTxBytes(uid);//上传的流量
               long used=getTotalBytesManual(uid);
           // if(rx!=-1&&tx!=-1){
                long oldAppBytes=pre.getLong(packageName,0);//昨天APP数据使用量//出错
                String old="old"+packageName;
                editor.putLong(old,oldAppBytes);//昨天使用量
               // editor.putString("date",date1);
              //  editor.putLong(packageName,rx+tx);//app流量
              // editor.putFloat(packageName,used);
            editor.putLong(packageName,used);
                editor.apply();
         //   }
        }
    }

    /**
     * 关闭手机时各应用流量清零
     */
    public static void setAppMobilePowerOff(Context context){
        SharedPreferences pre=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();
        PackageManager packageManager=context.getPackageManager();
        List<ApplicationInfo> applicationInfos=packageManager.getInstalledApplications(0);
        for(ApplicationInfo applicationInfo:applicationInfos){
            editor.putLong(applicationInfo.packageName,0);
            editor.putLong("today"+applicationInfo.packageName,0);//关闭手机时今日流量清零
        }
        editor.apply();
    }

    /**
     * 依据包名获取除今天外使用的量
     * @param context
     * @param packageName
     * @return
     */
    public static long getSelectedPackageUsed(Context context,String packageName){
        SharedPreferences pre=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE);
      //  long used=pre.getLong(packageName,-1);////出错
        long used;
        if(pre.getLong(packageName,-1)<0){
            used=0;
        }else{
            used=pre.getLong(packageName,-1);
        }
        return used;
    }

    /**
     *设置套餐流量
     * @param number 流量套餐
     * @param unit 单位是MB还是GB
     * @param day:每月流量计日
     */
    public static void setFlow(Context context,int number,String unit,int day){
        SharedPreferences pre=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();
        if(number>0){
            editor.putBoolean("hasNumber",true);//设置了套餐流量
            editor.putInt("flowNumber",number);
            editor.putInt("day",day);//计日
            editor.putString("unit",unit);//单位
            editor.apply();
        }else{
            editor.putBoolean("hasNumber",false);//未设置套餐流量
            editor.apply();
        }

    }


    /**
     * 按时间顺序获取最近31天所有总流量记录
     */
    public static List<FlowUsedRecorder> getAllDataFrom(Context context){
       // SharedPreferences pre=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE);
      //  Boolean hasNumber=pre.getBoolean("hasNumber",false);
       // SimpleDateFormat df = new SimpleDateFormat("dd");//设置日期格式
       // int nowDay=Integer.parseInt(df.format(new Date()));//日期几号
        //int day=pre.getInt("day",1);//每月计时日期
        List<FlowUsedRecorder> flow=DataSupport.findAll(FlowUsedRecorder.class);
        List<FlowUsedRecorder> flowUsedRecorders=DataSupport.order("date desc").find(FlowUsedRecorder.class);//按日期降序获取流量使用记录
        if(flowUsedRecorders.size()>31){
         for(int i=31;i<flowUsedRecorders.size();i++){//只保留最近31天记录
             FlowUsedRecorder flowUsedRecorder=flowUsedRecorders.get(i);
             flowUsedRecorder.delete();
             flowUsedRecorders.remove(i);
             i--;
        }
        }
        return flowUsedRecorders;

    }


    /**
     * 按时间顺序获取最近31天所有总移动流量记录
     */
    public static List<MobileUsedRecorder> getAllMobileFrom(Context context){
        SharedPreferences pre=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE);
        Boolean hasNumber=pre.getBoolean("hasNumber",false);
        SimpleDateFormat df = new SimpleDateFormat("d");//设置日期格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        int nowDay=Integer.parseInt(df.format(new Date()));//日期几号
        int day=pre.getInt("day",1);//每月计时日期
        List<MobileUsedRecorder> mobileUsedRecordersList=new ArrayList<MobileUsedRecorder>();
        List<MobileUsedRecorder> mobiles=DataSupport.findAll(MobileUsedRecorder.class);
        List<MobileUsedRecorder> mobileUsedRecorders=DataSupport.order("date desc").find(MobileUsedRecorder.class);//按日期降序获取流量使用记录
        if(mobileUsedRecorders.size()>31){
            for(int i=31;i<mobileUsedRecorders.size();i++){//只保留最近31天记录
                MobileUsedRecorder mobileUsedRecorder=mobileUsedRecorders.get(i);
                mobileUsedRecorder.delete();
                mobileUsedRecorders.remove(i);
                i--;
            }
        }
        if(hasNumber){//设置类套餐
            if(day<nowDay){
                int j=nowDay-day+1;
                Date date=null;
                for(int i=0;i<mobileUsedRecorders.size();i++){
                    try{
                        date=simpleDateFormat.parse(mobileUsedRecorders.get(i).getDate());
                    }catch (ParseException e){

                    }
                    int dayeInt=Integer.parseInt(df.format(date));
                    if(dayeInt>=day){
                        mobileUsedRecordersList.add(mobileUsedRecorders.get(i));
                    }else{
                        break;
                    }
                }
                return mobileUsedRecordersList;
            }else if(day>nowDay){
                int k=day-nowDay;
                Calendar c=Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.MONTH,-1);
                c.add(Calendar.DATE,k);
                Date result=c.getTime();
                String last=simpleDateFormat.format(result);
                for(int i=0;i<mobileUsedRecorders.size();i++){
                    //String date1=simpleDateFormat.format(mobileUsedRecorders.get(i).getDate());
                    String date1=mobileUsedRecorders.get(i).getDate();
                    if(!date1.equals(last)){
                        mobileUsedRecordersList.add(mobileUsedRecorders.get(i));
                    }else{
                        mobileUsedRecordersList.add(mobileUsedRecorders.get(i));//找到相同的日期添加后跳出循环
                        break;
                    }
                }
                return mobileUsedRecordersList;
            }else if(day==nowDay){
                String today=simpleDateFormat.format(new Date());
               // Date date=null;
                if(mobileUsedRecorders.size()!=0){
                   /* try{
                        date=simpleDateFormat.parse(mobileUsedRecorders.get(0).getDate());
                    }catch (ParseException e){

                    }*/
                   // Date d=mobileUsedRecorders.get(0).getDate();
                  //  String da=simpleDateFormat.format(date);
                    String da=mobileUsedRecorders.get(0).getDate();
                    if(today.equals(da)){
                        mobileUsedRecordersList.add(mobileUsedRecorders.get(0));
                    }
                }
                return mobileUsedRecordersList;
            }
        }else{//无套餐，返回本月1号开始的数据
            int a=nowDay-1;
            Calendar c=Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE,-a);
            Date result=c.getTime();
            String monthStart=simpleDateFormat.format(result);//月初
            for(int i=0;i<mobileUsedRecorders.size();i++){
               // String date1=simpleDateFormat.format(mobileUsedRecorders.get(i).getDate());
                String date1=mobileUsedRecorders.get(i).getDate();
                if(!date1.equals(monthStart)){
                    mobileUsedRecordersList.add(mobileUsedRecorders.get(i));
                }else{
                    mobileUsedRecordersList.add(mobileUsedRecorders.get(i));//找到相同的日期添加后跳出循环
                    break;
                }
            }
            return mobileUsedRecordersList;//返回本月初到今天的记录数据
        }
        return mobileUsedRecordersList;
    }


    /**
     * 通过反射机制设置移动网络
     * @param pContext
     * @param pBoolean 为true，开启移动网络，为false关闭网络
     */
    public static void setMobileData(Context pContext, boolean pBoolean) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();
            Class[] argsClass = new Class[1];
            argsClass[0] = boolean.class;
            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);
            method.invoke(mConnectivityManager, pBoolean);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("移动数据设置错误: " + e.toString());
        }
    }


    /**
     * 获取手机目前移动开关状态
     * @param pContext
     * @param arg 默认添null
     * @return true为链接，false 为没链接
     */
    public static boolean getMobileDataState(Context pContext, Object[] arg) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();
            Class[] argsClass = null;
            if (arg != null) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }
            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);
            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);
            return isOpen;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("得到移动数据状态出错");
            return false;
        }
    }


    /**
     * 获取本天流量使用情况
     * @return
     */
    public static List<MobileUsedRecorder> getThisDayUsedTotalMobile(){
        Date date=getDate(new Date());//年月日
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String date1=df.format(date);
        List<MobileUsedRecorder> mobileUsedRecorders= DataSupport.where("date = ?",date1).find(MobileUsedRecorder.class);
        return mobileUsedRecorders;
    }




    /**
     * 设置各应用今日所用流量
     */
    public static void setAppTodayMobilePowerOff(Context context){
        SharedPreferences pre=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();
        PackageManager packageManager=context.getPackageManager();
        List<ApplicationInfo> applicationInfos=packageManager.getInstalledApplications(0);
        for(ApplicationInfo applicationInfo:applicationInfos){
            Long todayUsed=getTotalBytesManual(applicationInfo.uid);
            editor.putLong("today"+applicationInfo.packageName,todayUsed);//今日流量
        }
        editor.apply();
    }











}
