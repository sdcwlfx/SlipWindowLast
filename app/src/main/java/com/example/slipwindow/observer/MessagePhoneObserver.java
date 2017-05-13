package com.example.slipwindow.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.example.slipwindow.entity.Message;

import java.util.ArrayList;

/**
 * 观察方法，监听短信数据库,获取所有短信
 * Created by asus on 2017-04-14.
 */

public class MessagePhoneObserver extends ContentObserver {
    private Context context;
    private ArrayList<Message> messagesArrayList=new ArrayList<Message>();

    public MessagePhoneObserver(Handler handler,Context context){
        super(handler);
        this.context=context;
    }

    public void onChange(boolean selfChange){
        super.onChange(selfChange);
        getMessageFromPhone();

    }

    /**
     * 获取所有短信，彩信
     */
    public void getMessageFromPhone(){
        Message message=new Message();
        Cursor cursor=null;
        try{
            //调用内容提供器
            cursor=context.getContentResolver().query(Uri.parse("content://sms/"),null,null,null,"datedesc");
            // boolean hasDone=false;
            if(cursor!=null)
            {
                while (cursor.moveToNext()){
                    String address=cursor.getString(cursor.getColumnIndex("address"));
                    String person=cursor.getString(cursor.getColumnIndex("person"));
                    long date=cursor.getLong(cursor.getColumnIndex("date"));
                    int read=cursor.getInt(cursor.getColumnIndex("read"));
                    int type=cursor.getInt(cursor.getColumnIndex("type"));
                    String body=cursor.getString(cursor.getColumnIndex("body"));
                    int protocol=cursor.getInt(cursor.getColumnIndex("protocol"));
                    message.setAddress(address);
                    message.setBody(body);
                    message.setDate(date);
                    message.setPerson(person);
                    message.setProtocal(protocol);
                    message.setRead(read);
                    message.setType(type);
                    messagesArrayList.add(message);
                }
            }
        }catch (Exception e){

        }

    }

    public ArrayList<Message> getMessagesArrayList(){
        return messagesArrayList;
    }


}
