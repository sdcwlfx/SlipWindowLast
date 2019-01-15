package com.example.slipwindow.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.example.slipwindow.util.TasksUtil;

public class ScreenOffService extends Service {
    private IntentFilter intentFilter;
    private ScreenOffReceiver screenOffReceiver;
    public ScreenOffService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate(){
        super.onCreate();
        intentFilter=new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        screenOffReceiver=new ScreenOffReceiver();
        registerReceiver(screenOffReceiver,intentFilter);

    }

    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(screenOffReceiver);//取消注册
    }

    //屏幕关闭接收器
    class ScreenOffReceiver extends BroadcastReceiver{
        public void onReceive(Context context,Intent intent){
           // if(pre.getBoolean("taskSetting",false)){
                // TasksUtil.CloseAllTasks(context);//关闭除本应外其它用户应用进程
                TasksUtil.CloseTasks(context);//获取root关闭进程
                //Toast.makeText(context,"关闭进程",Toast.LENGTH_LONG).show();
         //   }
        }
    }
}
