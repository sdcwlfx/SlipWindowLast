package com.example.slipwindow.broadcast;

/**
 * 监听来电广播，拦截黑名单来电或选中时间段来电
 */

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.example.slipwindow.db.PhoneDarkListHarass;
import com.example.slipwindow.db.PhoneHarrassRecorder;
import com.example.slipwindow.db.PhoneWhiteListPass;
import com.android.internal.telephony.ITelephony;
import com.example.slipwindow.db.SightModleTime;
import com.example.slipwindow.db.TimePhone;
import com.example.slipwindow.util.Common;

import org.litepal.crud.DataSupport;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PhoneStateReceiver extends BroadcastReceiver {
    private String phoneNumber;
    private List<PhoneHarrassRecorder> phoneHarrassRecorders;//所有拦截电话记录
    private List<PhoneDarkListHarass> phoneDarkListHarasses;//电话黑名单
    private List<PhoneWhiteListPass> phoneWhiteListPasses;//电话白名单
    private List<SightModleTime> sightModleTimeList;//所有时间段对象
    public PhoneStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))//去电
        {
           //去电无动作
        }
        else {//来电
            TelephonyManager telephonyManager=(TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
            SharedPreferences preferences=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE);
            int select=preferences.getInt("select",0);
            if(telephonyManager.getCallState()==TelephonyManager.CALL_STATE_RINGING)//来电响
            {
                phoneNumber=intent.getStringExtra("incoming_number");//电话
                Toast.makeText(context,phoneNumber+"Select:"+select,Toast.LENGTH_SHORT).show();
                if(select==1)//标准模式，仅拦黑名单
                {
                   phoneDarkListHarasses= DataSupport.findAll(PhoneDarkListHarass.class);//黑名单号码
                    for(PhoneDarkListHarass phoneDarkListHarass:phoneDarkListHarasses)
                    {
                        if(phoneDarkListHarass.getPhoneNum().equals(phoneNumber))
                        {
                            harrassPhone(context,phoneNumber);
                            break;
                        }
                    }
                }else if(select==2){//全开

                }else if(select==3){//全拦
                    harrassPhone(context,phoneNumber);
                }else if(select==4)//在白名单中且不在黑名单中，通过
                {
                    phoneDarkListHarasses= DataSupport.findAll(PhoneDarkListHarass.class);//黑名单号码
                    phoneWhiteListPasses=DataSupport.findAll(PhoneWhiteListPass.class);//白名单
                    PhoneDarkListHarass phoneDarkListHarass=new PhoneDarkListHarass();
                    phoneDarkListHarass.setPhoneNum(phoneNumber);
                    PhoneWhiteListPass phoneWhiteListPass=new PhoneWhiteListPass();
                    phoneWhiteListPass.setPhoneNum(phoneNumber);
                    if(!phoneDarkListHarasses.contains(phoneDarkListHarass)&&phoneWhiteListPasses.contains(phoneWhiteListPass)){
                        harrassPhone(context,phoneNumber);
                    }


                }else{//情景模式
                    sightModleTimeList=DataSupport.where("state=?","已开启").find(SightModleTime.class,true);//所有开启状态时间段对象
                    if(sightModleTimeList.size()>0){
                        final Calendar c=Calendar.getInstance();
                        int hour=c.get(Calendar.HOUR_OF_DAY);//当前小时
                        int minute=c.get(Calendar.MINUTE);//当前分钟
                        for(SightModleTime sightModleTime:sightModleTimeList)
                        {
                            if(sightModleTime.getStartHourTime()>sightModleTime.getEndHourTime()){//开始时钟大于结束时钟，，结束时钟表明是明天的时间，
                                if(hour>sightModleTime.getStartHourTime()){//目前时钟大于开始时钟
                                    if(sightModleTime.getTimePhones().size()>0){
                                        ArrayList<TimePhone> timePhones=sightModleTime.getTimePhones();
                                        for(TimePhone timePhone:timePhones){
                                            if(timePhone.getPhoneNum().equals(phoneNumber)){
                                                harrassPhone(context,phoneNumber);//拦截
                                            }
                                        }
                                    }
                                }else if(hour==sightModleTime.getStartHourTime()&&minute>sightModleTime.getStartMinuteTime()){//目前时钟与开始时钟相等，比较分钟
                                    if(sightModleTime.getTimePhones().size()>0){
                                        ArrayList<TimePhone> timePhones=sightModleTime.getTimePhones();
                                        for(TimePhone timePhone:timePhones){
                                            if(timePhone.getPhoneNum().equals(phoneNumber)){
                                                harrassPhone(context,phoneNumber);//拦截
                                            }
                                        }
                                    }

                                }else if(hour<sightModleTime.getEndHourTime()){//目前时钟小于结束时钟
                                    if(sightModleTime.getTimePhones().size()>0){
                                        ArrayList<TimePhone> timePhones=sightModleTime.getTimePhones();
                                        for(TimePhone timePhone:timePhones){
                                            if(timePhone.getPhoneNum().equals(phoneNumber)){
                                                harrassPhone(context,phoneNumber);//拦截
                                            }
                                        }
                                    }
                                }else if(hour==sightModleTime.getEndHourTime()&&minute<sightModleTime.getEndMinuteTime()){
                                    if(sightModleTime.getTimePhones().size()>0){
                                        ArrayList<TimePhone> timePhones=sightModleTime.getTimePhones();
                                        for(TimePhone timePhone:timePhones){
                                            if(timePhone.getPhoneNum().equals(phoneNumber)){
                                                harrassPhone(context,phoneNumber);//拦截
                                            }
                                        }
                                    }
                                }

                            }else if(sightModleTime.getStartHourTime()<sightModleTime.getEndHourTime()&&hour>=sightModleTime.getStartHourTime()&&hour<=sightModleTime.getEndHourTime()){
                                if(hour>sightModleTime.getStartHourTime()&&hour<sightModleTime.getEndHourTime()){
                                    if(sightModleTime.getTimePhones().size()>0){
                                        ArrayList<TimePhone> timePhones=sightModleTime.getTimePhones();
                                        for(TimePhone timePhone:timePhones){
                                            if(timePhone.getPhoneNum().equals(phoneNumber)){
                                                harrassPhone(context,phoneNumber);//拦截
                                            }
                                        }
                                    }
                                }else if(hour==sightModleTime.getStartHourTime()){
                                    if(minute>sightModleTime.getStartMinuteTime()){
                                        if(sightModleTime.getTimePhones().size()>0){
                                            ArrayList<TimePhone> timePhones=sightModleTime.getTimePhones();
                                            for(TimePhone timePhone:timePhones){
                                                if(timePhone.getPhoneNum().equals(phoneNumber)){
                                                    harrassPhone(context,phoneNumber);//拦截
                                                }
                                            }
                                        }
                                    }
                                }else if(hour==sightModleTime.getEndHourTime()){
                                    if(minute<sightModleTime.getEndMinuteTime()){
                                        if(sightModleTime.getTimePhones().size()>0){
                                            ArrayList<TimePhone> timePhones=sightModleTime.getTimePhones();
                                            for(TimePhone timePhone:timePhones){
                                                if(timePhone.getPhoneNum().equals(phoneNumber)){
                                                    harrassPhone(context,phoneNumber);//拦截
                                                }
                                            }
                                        }
                                    }
                                }
                            }else if(sightModleTime.getStartHourTime()==sightModleTime.getEndHourTime()&&hour==sightModleTime.getStartHourTime()){//小时相同
                                if(sightModleTime.getEndMinuteTime()>sightModleTime.getStartMinuteTime()){//结束分钟大
                                    if(minute>sightModleTime.getStartMinuteTime()&&minute<sightModleTime.getEndMinuteTime()){
                                        if(sightModleTime.getTimePhones().size()>0){
                                            ArrayList<TimePhone> timePhones=sightModleTime.getTimePhones();
                                            for(TimePhone timePhone:timePhones){
                                                if(timePhone.getPhoneNum().equals(phoneNumber)){
                                                    harrassPhone(context,phoneNumber);//拦截
                                                }
                                            }
                                        }
                                    }
                                }else{//结束分钟小于开始分钟
                                    if(minute>sightModleTime.getStartMinuteTime()){
                                        if(sightModleTime.getTimePhones().size()>0){
                                            ArrayList<TimePhone> timePhones=sightModleTime.getTimePhones();
                                            for(TimePhone timePhone:timePhones){
                                                if(timePhone.getPhoneNum().equals(phoneNumber)){
                                                    harrassPhone(context,phoneNumber);//拦截
                                                }
                                            }
                                        }
                                    }else if(minute<sightModleTime.getEndMinuteTime()){//
                                        if(sightModleTime.getTimePhones().size()>0){
                                            ArrayList<TimePhone> timePhones=sightModleTime.getTimePhones();
                                            for(TimePhone timePhone:timePhones){
                                                if(timePhone.getPhoneNum().equals(phoneNumber)){
                                                    harrassPhone(context,phoneNumber);//拦截
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                           /* if(sightModleTime.getTimePhones().size()>0){
                                ArrayList<TimePhone> timePhones=sightModleTime.getTimePhones();
                                for(TimePhone timePhone:timePhones){
                                    if(timePhone.getPhoneNum().equals(phoneNumber)){
                                        harrassPhone(context,phoneNumber);//拦截
                                    }
                                }
                            }*/
                        }
                    }
                }
            }
        }

    }

    /**
     * 拦截电话
     * @param context
     * @param phoneNumber
     */
    public void harrassPhone(Context context,String phoneNumber){
        Date date=new Date();
        String time=dateFormat(date);//时间
        stopCall(phoneNumber,context);//结束电话
        abortBroadcast();//截断广播
        addPhoneHarrassRecord(phoneNumber,time,context);//添加拦截记录
        Toast.makeText(context,"拦截了一个电话",Toast.LENGTH_SHORT).show();
    }

    /**
     * 标准日期
     * @param timeDate
     * @return
     */
    public String  dateFormat(Date timeDate){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time=simpleDateFormat.format(timeDate);//时间
        return time;
    }

    /**
     * 添加电话拦截记录
     * @param phoneNumber
     * @param time
     */
    public void addPhoneHarrassRecord(String phoneNumber,String time,Context context){
        String name= Common.getNameOnAddress(context,phoneNumber);
        PhoneHarrassRecorder phoneHarrassRecorder=new PhoneHarrassRecorder();
        phoneHarrassRecorder.setAddress(phoneNumber);
        phoneHarrassRecorder.setDate(time);
        phoneHarrassRecorder.setName(name);
        phoneHarrassRecorder.save();//添加电话拦截记录
    }

    /**
     * 电话拦截
     * @param phoneNumber
     */
    public void stopCall(String phoneNumber,Context context){
        AudioManager audioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);//媒体管理器
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);//静音
        ITelephony iTelephony=getITelephony(context);
        try{
            iTelephony.endCall();//结束电话
            Toast.makeText(context,"结束电话",Toast.LENGTH_SHORT).show();///////////////
        }catch (Exception e){

        }
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);//回复正常铃声


    }

    /**
     * 通过AIDL获取ITelephony对象（是安卓Phone类中TelephonyManager提供给上层应用程序用户与telephony进行操作交互的接口）
     * @param context
     * @return
     */
    private static ITelephony getITelephony(Context context){
        TelephonyManager telephonyManager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        Class c=TelephonyManager.class;
        Method getITelephonyMethod=null;
        ITelephony iTelephony=null;
        try{
            getITelephonyMethod=c.getDeclaredMethod("getITelephony",(Class[])null);//获取声明的方法
            getITelephonyMethod.setAccessible(true);
        }catch (Exception e){

        }
        try{
            iTelephony=(ITelephony)getITelephonyMethod.invoke(telephonyManager,(Object[])null);//获取实例
            return iTelephony;

        }catch (Exception e){

        }
        return iTelephony;


    }






}
