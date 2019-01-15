package com.example.slipwindow.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by asus on 2017-05-28.
 */

public class RubbishClear {
    /**
     * 申请过多内存，迫使系统释放其它应用占用内存
     * @param context
     */
    private long totalCacheSize=0;
    public static void ClearCache(Context context) {
        try {
            PackageManager packageManager=context.getPackageManager();
            Class[] arrayOfClass = new Class[2];
            Class localClass2 = Long.TYPE;
            arrayOfClass[0] = localClass2;
            arrayOfClass[1] = IPackageDataObserver.class;
            Method method = PackageManager.class.getMethod("freeStorageAndNotify", arrayOfClass);
            method.invoke(packageManager,getEnvironmentSize() - 1L, new IPackageDataObserver.Stub() {
                @Override
                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                    System.out.println("successed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    private static long getEnvironmentSize() {
        File localFile = Environment.getDataDirectory();
        long l1;
        if (localFile == null)
            l1 = 0L;
        while (true) {
            String str = localFile.getPath();
            StatFs localStatFs = new StatFs(str);
            long l2 = localStatFs.getBlockSize();
            l1 = localStatFs.getBlockCount() * l2;
            return l1;
        }
    }

    public static void getAllMemory(Context context) throws Exception{
        PackageManager packageManager=context.getPackageManager();
        Class[] arrayOfClass=new Class[2];
        Class localClass2=Long.TYPE;
        arrayOfClass[0]=localClass2;
        arrayOfClass[1]=IPackageDataObserver.class;
        Method method = PackageManager.class.getMethod("freeStorageAndNotify", arrayOfClass);
        Long loacalLong=Long.valueOf(getEnvironmentSize()-1L);
        Object[] arrayOfObject=new Object[2];
        arrayOfObject[0]=loacalLong;
        method.invoke(packageManager,loacalLong, new IPackageDataObserver.Stub() {
            @Override
            public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

            }
        });
    }



    private void queryTotalCache(Context context){
        PackageManager packageManager=context.getPackageManager();
        List<ApplicationInfo> applicationInfos=packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        String packageName="";
        for(ApplicationInfo applicationInfo:applicationInfos){
            packageName=applicationInfo.packageName;
            try{
                queryPackageCacheSize(context,packageName);
            }catch (Exception e){

            }
        }
    }

    public void queryPackageCacheSize(Context context,String packageName) throws Exception{
        if(!TextUtils.isEmpty(packageName)){
            PackageManager packageManager=context.getPackageManager();//
            try{
                String strGetPackageSizeInfo="getPackageSizeInfo";
                Method method=packageManager.getClass().getDeclaredMethod(strGetPackageSizeInfo,String.class,IPackageStatsObserver.class);
                method.invoke(packageManager,packageName,mStatsObserver);
            }catch (Exception e){
            }
        }
    }

    private IPackageStatsObserver.Stub mStatsObserver=new IPackageStatsObserver.Stub() {
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            long tmp=totalCacheSize;
            totalCacheSize+=pStats.cacheSize;
            if(tmp!=totalCacheSize){

            }
        }
    };

    //返回总缓存
    public long getTotalCacheSize(){
        return totalCacheSize;
    }

















}
