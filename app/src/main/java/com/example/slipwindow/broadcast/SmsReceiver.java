package com.example.slipwindow.broadcast;

/**
 * 响应普通短信广播,使该应用成为默认短信接受
 */

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;
import android.content.SharedPreferences;
import com.example.slipwindow.db.MessageDarkListHarass;
import com.example.slipwindow.db.MessageHarrassRecorder;
import com.example.slipwindow.db.MessageWhiteListPass;
import com.example.slipwindow.entity.Contact;
import com.example.slipwindow.util.Common;
import com.example.slipwindow.util.SensitiveWordFilter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class SmsReceiver extends BroadcastReceiver {
    private List<MessageDarkListHarass> messageDarkListHarasses;//短信黑名单
    private List<MessageWhiteListPass> messageWhiteListPasses;//短信白名单
    private ArrayList<Contact> contacts;//所有联系人
    private SensitiveWordFilter wordFilter;//关键词拦截
    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        Bundle bundle=intent.getExtras();
        if(bundle==null)
        {
            return;
        }
        Object[] pdus=(Object[])bundle.get("pdus");
        if(pdus==null||pdus.length==0)
        {
            return;
        }
        messageDarkListHarasses= DataSupport.findAll(MessageDarkListHarass.class);
        messageWhiteListPasses=DataSupport.findAll(MessageWhiteListPass.class);
        wordFilter=new SensitiveWordFilter(context);
        int checked=getCheckedRadioButton(context);
        ContentResolver contentResolver=context.getContentResolver();//内容访问器
        for(Object pdu:pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            ContentValues contentValues=new ContentValues();
            String name=getNameOnAddress(context,smsMessage.getOriginatingAddress());
            Toast.makeText(context,"获取关键词规格："+wordFilter.sensitiveWordMap.size(),Toast.LENGTH_SHORT).show();
            if(wordFilter.isContainSensitiveWord(smsMessage.getMessageBody(),1)){//最小匹配规则,若包含拦截关键词则拦截//关键词黑名单拦截
                addMessageToHarrass(context, smsMessage, contentValues, contentResolver,name);
                Toast.makeText(context,"无匹配",Toast.LENGTH_SHORT).show();
                break;
            }
            if(checked==2){
                if(messageDarkListHarasses.size()>0){
                    int i=0;
                    for(;i<messageDarkListHarasses.size();i++){
                    MessageDarkListHarass messageDarkListHarass=messageDarkListHarasses.get(i);
                        if(smsMessage.getOriginatingAddress().equals(messageDarkListHarass.getPhoneNum())) {//拦截
                            addMessageToHarrass(context, smsMessage, contentValues, contentResolver,name);
                            break;
                        }
                   }
                    if(i>=messageDarkListHarasses.size()){
                        addMessageToPhone(context,smsMessage,contentValues,contentResolver);
                    }

                }else{
                    addMessageToPhone(context,smsMessage,contentValues,contentResolver);

                }
            }else if(checked==1){
                if(messageWhiteListPasses.size()>0){
                    int i=0;
                    for(;i<messageWhiteListPasses.size();i++) {
                        MessageWhiteListPass messageWhiteListPass = messageWhiteListPasses.get(i);
                        if (smsMessage.getOriginatingAddress().equals(messageWhiteListPass.getPhone())) {//通过
                            addMessageToPhone(context, smsMessage, contentValues, contentResolver);
                            break;
                        }
                    }
                    if(i>=messageWhiteListPasses.size()){
                        addMessageToHarrass(context,smsMessage,contentValues,contentResolver,name);
                    }
                }
                else{
                    addMessageToHarrass(context,smsMessage,contentValues,contentResolver,name);
                }
            }
        }

    }


    /**
     * 获取选中的模式1、2、
     * @return
     */
    public int getCheckedRadioButton(Context context){
        SharedPreferences preferences=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE);
        int checked=preferences.getInt("selectMessage",0);
        return checked;
    }

    /**
     * 不拦截的短信
     * @param context
     * @param smsMessage
     * @param contentValues
     * @param contentResolver
     */
    public void addMessageToPhone(Context context,SmsMessage smsMessage, ContentValues contentValues,ContentResolver contentResolver){
        Toast.makeText(context,smsMessage.getOriginatingAddress(),Toast.LENGTH_SHORT).show();
        contentValues.put(Telephony.Sms.ADDRESS,smsMessage.getOriginatingAddress());//地址
        contentValues.put(Telephony.Sms.BODY,smsMessage.getMessageBody());//内容
        contentValues.put(Telephony.Sms.DATE,smsMessage.getTimestampMillis());//时间
        contentValues.put(Telephony.Sms.READ,0);//未读
        contentValues.put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_INBOX);//收到的短信
        contentResolver.insert(Uri.parse("content://sms"),contentValues);
    }

    /**
     * 拦截短信
     * @param context
     * @param smsMessage
     * @param contentValues
     * @param contentResolver
     */
    public void addMessageToHarrass(Context context,SmsMessage smsMessage, ContentValues contentValues,ContentResolver contentResolver,String name){
        Toast.makeText(context,"拒收"+smsMessage.getOriginatingAddress()+"短信",Toast.LENGTH_SHORT).show();
        MessageHarrassRecorder messageHarrass=new MessageHarrassRecorder();
        messageHarrass.setBody(smsMessage.getMessageBody());//内容
        messageHarrass.setAddress(smsMessage.getOriginatingAddress());//地址
        messageHarrass.setType(Telephony.Sms.MESSAGE_TYPE_INBOX);//收到短信
        messageHarrass.setRead(0);//未读
        messageHarrass.setName(name);
        messageHarrass.setDate(smsMessage.getTimestampMillis());//时间
        messageHarrass.save();//添加到数据库表
    }

    /**
     * 返回姓名
     * @param context
     * @param address
     * @return
     */

    public String getNameOnAddress(Context context,String address ){

        contacts= Common.loadAllContactsFromPhone(context);//所有联系人
        for(Contact contact:contacts){
            if(contact.getAddress().equals(address)){
                return contact.getName();
            }
        }
        return "";
    }
}
