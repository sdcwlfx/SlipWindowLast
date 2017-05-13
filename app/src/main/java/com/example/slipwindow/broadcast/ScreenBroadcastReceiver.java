package com.example.slipwindow.broadcast;
/**
 * 接听屏幕状态广播
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
        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            SharedPreferences pre=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE);
            if(pre.getBoolean("taskSetting",false)){
               // TasksUtil.CloseAllTasks(context);//关闭除本应外其它用户应用进程
                TasksUtil.CloseTasks(context);
                Toast.makeText(context,"关闭进程",Toast.LENGTH_LONG).show();
            }
        }

        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
