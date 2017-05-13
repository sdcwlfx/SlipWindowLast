package com.example.slipwindow.packageStatsObserver;

import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.content.pm.PackageStats;
import android.os.Looper;
import android.os.RemoteException;
import android.text.format.Formatter;

import android.widget.TextView;


/**
 * 依据上下文、包名获得应用大小、缓存大小Created by asus on 2017-03-13
 */

public class AppSize {
    private static  String appSize;
    public static void getAppSize(final Context context, String pkgName, final TextView appSizeText)
            throws NoSuchMethodException,InvocationTargetException,IllegalAccessException{
        Method method= PackageManager.class.getMethod("getPackageSizeInfo",String.class, IPackageStatsObserver.class);
        method.invoke(context.getPackageManager(),pkgName,new IPackageStatsObserver.Stub(){
            public void onGetStatsCompleted(PackageStats pStats,boolean succeeded) throws RemoteException{
               // Looper.prepare();
                appSize= Formatter.formatFileSize(context,pStats.codeSize);
                appSizeText.setText(appSize);
                //Looper.loop();
            }
        });

    }

}
