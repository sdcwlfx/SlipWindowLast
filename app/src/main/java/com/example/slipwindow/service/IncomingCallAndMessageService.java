package com.example.slipwindow.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class IncomingCallAndMessageService extends Service {
    private TelephonyManager telephonyManager;
    private MyPhoneStateListener myPhoneStateListener;
    private String phoneNumber;
    private PhoneNumberBinder phoneNumberBinder=new PhoneNumberBinder();

    public class PhoneNumberBinder extends Binder{
        public String getPhoneNumber(){
            return phoneNumber;
        }
    }

    public IncomingCallAndMessageService() {
    }

    /**
     * 服务创建调用
     */
    public void onCreate(){
        super.onCreate();
        getIncomingCall();//来电号码

    }

    /**
     * 获取来电号码
     */
    public void getIncomingCall(){
        //获取电话管理器
        telephonyManager=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        myPhoneStateListener=new MyPhoneStateListener();//设置电话状态监听器
        telephonyManager.listen(myPhoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);

    }


    /**
     * 与活动通信
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return phoneNumberBinder;
    }

    /**
     * 电话监听类
     */
    class MyPhoneStateListener extends PhoneStateListener{
        public void onCallStateChanged(int state,String incomingNumber){
            super.onCallStateChanged(state,incomingNumber);
            if (state==TelephonyManager.CALL_STATE_RINGING){
                //Toast.makeText(IncomingCallAndMessageService.this,incomingNumber,Toast.LENGTH_SHORT).show();
                phoneNumber=incomingNumber;//电话号码
            }


        }
    }


}
