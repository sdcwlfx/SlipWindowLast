package com.example.slipwindow.broadcast;
/**
 * 接受一天结束广播(自定义广播com.example.slipwindow.MY_FLOWMANAGE_BROADCAST)
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.slipwindow.util.FlowStorageManage;

public class MyFlowManageReceiver extends BroadcastReceiver {
    private long oldTotalBytes=0;
    private long oldMobileBytes=0;
    public MyFlowManageReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {//将所有应用的使用流量存入数据库
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
       // throw new UnsupportedOperationException("Not yet implemented");
        SharedPreferences pre=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();
        editor.putString("myFlow","接受了自定义广播");
        oldTotalBytes=pre.getLong("oldTotalBytes",0);//昨天总流量
        oldMobileBytes=pre.getLong("oldMobileBytes",0);//昨天总移动数据
        editor.apply();
        abortBroadcast();//截断广播
        FlowStorageManage.addAllFlowUsed(oldTotalBytes);//总流量更新
        Toast.makeText(context,"总流量更新",Toast.LENGTH_SHORT).show();
        FlowStorageManage.addMobileUsed(oldMobileBytes);//总移动数据更新
        Toast.makeText(context,"总移动数据更新",Toast.LENGTH_SHORT).show();
        FlowStorageManage.addMobileUsedSelectedAppAfterDay(context);//一天结束调用更新各App流量使用
        Toast.makeText(context,"各APP流量更新",Toast.LENGTH_SHORT).show();

    }
}
