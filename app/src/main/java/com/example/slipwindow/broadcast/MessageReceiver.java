package com.example.slipwindow.broadcast;

/**
 * 广播形式获取新消息
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.example.slipwindow.db.MessageHarrassRecorder;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MessageReceiver extends BroadcastReceiver {
    private static MessageListener messageListener;
    public static final String SMS_RECEIVED_ACTION="android.provider.Telephony.SMS_RECEIVED";
    public MessageReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        if(intent.getAction().equals(SMS_RECEIVED_ACTION)){
            Object[] pdus=(Object[])intent.getExtras().get("pdus");
            for(Object pdu:pdus)
            {
                SmsMessage smsMessage=SmsMessage.createFromPdu((byte[])pdu);
                String address=smsMessage.getDisplayOriginatingAddress();//地址
                String body=smsMessage.getDisplayMessageBody();//内容
                long date=smsMessage.getTimestampMillis();
                Date timeDate=new Date(date);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time=simpleDateFormat.format(timeDate);//时间
                MessageHarrassRecorder messageHarrass=new MessageHarrassRecorder();
                messageHarrass.setBody(body);
                messageHarrass.setAddress(address);
                messageHarrass.setDate(smsMessage.getTimestampMillis());
                messageListener.onReceived(messageHarrass);
                abortBroadcast();//断掉广播



            }
        }
    }

    //回调接口
    public interface MessageListener{
        void onReceived(MessageHarrassRecorder messageHarrass);
    }
    public void setOnReceivedMessageListener(MessageListener messageListener){
        this.messageListener=messageListener;
    }
}
