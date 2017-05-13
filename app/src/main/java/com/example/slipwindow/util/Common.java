package com.example.slipwindow.util;
/**
 * 通用方法
 */

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.example.slipwindow.db.PhoneDarkListHarass;
import com.example.slipwindow.db.SightModleTime;
import com.example.slipwindow.entity.Contact;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2017-04-18.
 */

public class Common {

    /**
     * 加载所有联系人
     */
    public static ArrayList<Contact> loadAllContactsFromPhone(Context context){
        Cursor cursor=null;
        ArrayList<Contact> contacts=new ArrayList<Contact>();
        // noSelectedContacts=new ArrayList<Contact>();

        try{
            cursor= context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
            if(cursor!=null){
                while (cursor.moveToNext()){
                    String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));//获取联系人姓名
                    String address=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));//获取联系人手机号
                    if(address.contains("-"))//规定电话格式
                        address=address.replace("-","");
                    if(address.contains(" "))
                        address=address.replace(" ","");
                    Contact contact=new Contact();
                    contact.setName(name);
                    contact.setAddress(address);
                    contacts.add(contact);
                    // noSelectedContacts.add(contact);
                }
            }
        }catch (Exception e){

        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        // Toast.makeText(PhoneDarkContactActivity.this,"获得所有联系人",Toast.LENGTH_SHORT).show();
        return contacts;
    }


    /**
     * 判定电话号码
     * @param num
     * @return
     */
    public static boolean isMobile(String num){
        String telRegex="[1][358]\\d{9}";
        if(TextUtils.isEmpty(num)){
            return false;
        }else{
            return num.matches(telRegex);
        }
    }

    /**
     * 获得选中的该时间段名单
     * @return
     */
    public static SightModleTime getSelectedTime(Bundle bundle){
        String startHourTime=String.valueOf(bundle.getInt("startHour"));
        String startMinuteTime=String.valueOf(bundle.getInt("startMinute"));
        String endHourTime=String.valueOf(bundle.getInt("endHour"));
        String endMinuteTime=String.valueOf(bundle.getInt("endMinute"));
        List<SightModleTime> list= DataSupport.where("startHourTime=? and startMinuteTime=? and endHourTime=? and endMinuteTime=?",startHourTime,startMinuteTime,endHourTime,endMinuteTime).find(SightModleTime.class,true);
        if(list.size()>=1)
            return list.get(0);
        else
            return null;
    }


    /**
     * 返回姓名
     * @param context
     * @param address
     * @return
     */

    public static String getNameOnAddress(Context context,String address ){

        ArrayList<Contact> contacts= Common.loadAllContactsFromPhone(context);//所有联系人
        for(Contact contact:contacts){
            if(contact.getAddress().equals(address)){
                return contact.getName();
            }
        }
        return "";
    }


    /**
     * 判断是否有root权限，有返回true
     * @return
     */
    public static boolean isRoot() {
        boolean bool = false;
        try {
            bool = new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }


}
