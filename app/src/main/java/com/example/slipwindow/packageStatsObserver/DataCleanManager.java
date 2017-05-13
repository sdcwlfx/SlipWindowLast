package com.example.slipwindow.packageStatsObserver;
/**
 * 清理应用数据,及缓存
 */

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;
import java.io.DataOutputStream;
import java.io.File;


/**
 * Created by asus on 2017-03-08.
 */

public class DataCleanManager {
    /**
     * q清除本应用内部缓存/data/data/com..example.slipwindow/cache
     * @param context
     */
    public static void cleanInternalCache(Context context){
        deleteFilesByDirectory(context.getCacheDir());//将cache路径文件夹传进去
    }

    /**
     * 删除本应用文件夹directory下的文件，若指定文件不处理
     * @param
     */
    public static void deleteFilesByDirectory(File directory){
        if(directory!=null&&directory.exists()&&directory.isDirectory()){
            for(File file : directory.listFiles()){
                file.delete();//删除指定文件
            }
        }

    }

    /**
     * 清理本应用外部缓存
     * @param context
     */
    public static void cleanExternalCache(Context context){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * 清除files下的内容
     * @param context
     */
    public static void cleanFiles(Context context){
        deleteFilesByDirectory(context.getFilesDir());
    }

    /**
     * 清除应用所有数据库中数据如：sqllite
     * @param context
     */
    public static void cleanDatabases(Context context){
        deleteFilesByDirectory(new File("/data/data/"+context.getPackageName()+"/databases"));
    }

    /**
     * q清除键值对形式存储的数据
     * @param context
     */
    public static void cleanSharedPreference(Context context){
        deleteFilesByDirectory(new File("/data/data"+context.getPackageName()+"/shared_prefs"));
    }


    /**
     * 清除本应用所有数据
     * @param context
     */
    public static void cleanApplicationData(Context context){
        cleanInternalCache(context);//缓存清理
        cleanExternalCache(context);//清理外部缓存
        cleanDatabases(context);//清除数据库数据
        cleanFiles(context);//清理files下内容
        cleanSharedPreference(context);//清除/data/data/com.example.slipwindow/shared_prefs下内容


    }


    /**
     *清理其它应用缓存
     */
    public static void cleanOtherAppCache(final Context context) {
        (new Thread() {
            public void run() {

                cleanOAppDerectory(context, context.getCacheDir());//内部cache清理
                cleanOAppDerectory(context, context.getExternalCacheDir());//外部cache清理
           /* try {
                File path = context.getCacheDir();
                if (path != null) {
                    String killer = "rm -r " + path.toString();
                    Process process = Runtime.getRuntime().exec("su");
                    DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
                    dataOutputStream.writeBytes(killer.toString() + "\n");
                    dataOutputStream.writeBytes("exit\n");
                    dataOutputStream.flush();
                }

            } catch (Exception e) {

            }
        }
        }).start();*/
            }
        }).start();
    }

    /**
     * 清理选中应用数据
     * @param context
     */
    public static void cleanOtherAppData(final Context context){
        (new Thread(){
            public void run(){
                cleanOAppDerectory(context,new File("/data/data/"+context.getPackageName()+"/databases"));
                cleanOAppDerectory(context,new File("/data/data"+context.getPackageName()+"/shared_prefs"));
                cleanOAppDerectory(context,context.getFilesDir());
                cleanOAppDerectory(context,new File("/storage/sdcard0/Android/data/"+context.getPackageName()+"/files"));
               // cleanOAppDerectory(context,context.getExternalCacheDir());//SD缓存
                cleanOtherAppCache(context);//清内外缓存
                //cleanOAppDerectory(context,new File("/data/data/"+context.getPackageName()));
                //cleanOAppDerectory(context,new File("/storage/sdcard0/Android/data/"+context.getPackageName()));
            }
        }).start();
    }

    /**
     * 清理指定路径文件，需获取Root权限
     * @param context
     * @param path
     */
    public static void cleanOAppDerectory(final Context context,File path){
        try{
            if (path != null) {
                String killer = "rm -r " + path.toString();
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
                dataOutputStream.writeBytes(killer.toString() + "\n");
                dataOutputStream.writeBytes("exit\n");
                dataOutputStream.flush();
            }

        }catch (Exception e){

        }
    }

}
