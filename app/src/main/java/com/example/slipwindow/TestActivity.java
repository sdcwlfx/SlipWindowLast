package com.example.slipwindow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.slipwindow.service.FlowManageService;
import com.example.slipwindow.util.FlowStorageManage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestActivity extends AppCompatActivity {
    private Button testFlowButton;
    private Button testPhoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        testFlowButton=(Button)findViewById(R.id.test_my_flow_button);
        testPhoneButton=(Button)findViewById(R.id.test_phone_button);
        testFlowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("mm");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                Date nowDate=new Date();
                String date=df.format(nowDate);
                String time=simpleDateFormat.format(nowDate);
                // if(time.equals("23:58")){//过了一天，发送自定义广播存储各应用流量使用
                //if(time.equals("05")){
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
                    FlowStorageManage.setAllAppMobileUsed(TestActivity.this,date);
                    //  }
                    Intent intent=new Intent("com.example.slipwindow.MY_FLOWMANAGE_BROADCAST");//发送有序广播
                    sendOrderedBroadcast(intent,null);
             //   }
            }
        });
        testPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//测试关机广播
                long oldTotalBytes=0;
                long oldMobileBytes=0;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                String nowDate=df.format(new Date());
                SharedPreferences pre=getSharedPreferences("phoneModle", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=pre.edit();//从新计前天流量
                editor.putString("phoneClose","接受了关机广播");
                editor.apply();
                String oldDate=pre.getString("date","");
                if(oldDate.equals(nowDate)){//同一天
                    //SharedPreferences.Editor editor=pre.edit();
                    editor.putLong("totalBytes",0);//总流量
                    editor.putLong("mobileBytes",0);//总移动数据
                    //editor.apply();
                }else{//不是同一天
                    oldTotalBytes=pre.getLong("totalBytes",0);
                    oldMobileBytes=pre.getLong("mobileBytes",0);
                }
                FlowStorageManage.addAllFlowUsed(oldTotalBytes);//总流量更新
                FlowStorageManage.addMobileUsed(oldMobileBytes);//总移动数据更新
                FlowStorageManage.addMobileUsedSelectedApp(TestActivity.this);//更新各App流量使用情况//出错
                editor.putLong("totalBytes",0);//总流量
                editor.putLong("mobileBytes",0);//总移动数据
                // editor.putBoolean("flowManage",true);
                editor.apply();
                FlowStorageManage.setAppMobilePowerOff(TestActivity.this);
            }
        });
    }
}
