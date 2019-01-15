package com.example.slipwindow.util;
/**
 * 通用方法
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.example.slipwindow.db.Permission;
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


    /**
     * 初始化所有权限
     */

    public static void initPermission(Context context){
        Permission permission1=new Permission();
        permission1.setPermission("android.permission.ACCESS_COARSE_LOCATION");
        permission1.setName("读取位置信息");
        permission1.save();
        Permission permission2=new Permission();
        permission2.setPermission("android.permission.ACCESS_FINE_LOCATION");
        permission2.setName("读取位置信息");
        permission2.save();
        Permission permission3=new Permission();
        permission3.setPermission("android.permission.ACCESS_LOCATION_EXTRA_COMMANDS");
        permission3.setName("读取位置信息");
        permission3.save();
        Permission permission4=new Permission();
        permission4.setPermission("android.permission.ACCESS_MOCK_LOCATION");
        permission4.setName("读取位置信息");
        permission4.save();
        Permission permission5=new Permission();
        permission5.setPermission("android.permission.ACCESS_NETWORK_STATE");
        permission5.setName("访问网络");
        permission5.save();
        Permission permission6=new Permission();
        permission6.setPermission("android.permission.BLUETOOTH");
        permission6.setName("蓝牙");
        permission6.save();
        Permission permission7=new Permission();
        permission7.setPermission("android.permission.CALL_PHONE");
        permission7.setName("电话");
        permission7.save();
        Permission permission8=new Permission();
        permission8.setPermission("android.permission.CALL_PRIVILEGED");
        permission8.setName("电话");
        permission8.save();
        Permission permission9=new Permission();
        permission9.setPermission("android.permission.DEVICE_POWER");
        permission9.setName("电源");
        permission9.save();
        Permission permission10=new Permission();
        permission10.setPermission("android.permission.MODIFY_AUDIO_SETTINGS");
        permission10.setName("声音");
        permission10.save();
        Permission permission11=new Permission();
        permission11.setPermission("android.permission.NFC");
        permission11.setName("NFC");
        permission11.save();
        Permission permission12=new Permission();
        permission12.setPermission("android.permission.READ_CONTACTS");
        permission12.setName("联系人");
        permission12.save();
        Permission permission13=new Permission();
        permission13.setPermission("android.permission.READ_SMS");
        permission13.setName("短信");
        permission13.save();
        Permission permission14=new Permission();
        permission14.setPermission("android.permission.RECEIVE_SMS");
        permission14.setName("短信");
        permission14.save();
        Permission permission15=new Permission();
        permission15.setPermission("android.permission.RECORD_AUDIO");
        permission15.setName("录音");
        permission15.save();
        Permission permission16=new Permission();
        permission16.setPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        permission16.setName("存储");
        permission16.save();
        Permission permission17=new Permission();
        permission17.setPermission("android.permission.READ_EXTERNAL_STORAGE");
        permission17.setName("存储");
        permission17.save();
        Permission permission18=new Permission();
        permission18.setPermission("android.permission.CAMERA");
        permission18.setName("调用摄像头");
        permission18.save();
        Permission permission19=new Permission();
        permission19.setPermission("android.permission.WRITE_CALENDAR");
        permission19.setName("日程");
        permission19.save();
        Permission permission20=new Permission();
        permission20.setPermission("android.permission.RECEIVE_BOOT_COMPLETED");
        permission20.setName("开机自启");
        permission20.save();
    }

    /**
     * 依据英文返回中文权限
     * @param permissionList
     * @return
     */
   // public static ArrayList<String> getSelectedAppPermission(String[] permissionList){
    public static ArrayList<String> getSelectedAppPermission(String[] permissionList){
        ArrayList<String> chinesePermisson=new ArrayList<String>();
        // String[] chinesePermissons=new String[permissionList.length];
        List<Permission> permissions=null;
        for(int i=0;i<permissionList.length;i++){
            permissions=DataSupport.where("permission=?",permissionList[i]).find(Permission.class);
            if(permissions.size()>0&&!chinesePermisson.contains(permissions.get(0).getName())){
                chinesePermisson.add(permissions.get(0).getName());
            }
        }
        return chinesePermisson;

    }


}
