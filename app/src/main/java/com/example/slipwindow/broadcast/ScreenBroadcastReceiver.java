package com.example.slipwindow.broadcast;
/**
 * 接听屏幕锁频状态广播
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.slipwindow.util.TasksUtil;

public class ScreenBroadcastReceiver extends BroadcastReceiver {
    public ScreenBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
            SharedPreferences pre=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();
        editor.putString("screenOff","接受到锁频广播");
        editor.apply();
            if(pre.getBoolean("taskSetting",false)){
               // TasksUtil.CloseAllTasks(context);//关闭除本应外其它用户应用进程
                TasksUtil.CloseTasks(context);
                //Toast.makeText(context,"关闭进程",Toast.LENGTH_LONG).show();
            }


        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
