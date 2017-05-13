package com.example.slipwindow.util;
/**
 * 封装任务管理正在运行的进程信息
 */

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.os.Process;
import android.widget.Toast;
import com.example.slipwindow.R;
import com.example.slipwindow.View.MyWindowManager;
import com.example.slipwindow.entity.TaskInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2017-04-26.
 */

public class TasksUtil {
    public static java.lang.Process process;

    /**
     * 获取当前正在运行的进程数
     * @param context
     * @return
     */
    public static int getRunningAppProgressInfoSize(Context context){
        ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningAppProcesses().size();
    }

    /**
     * 获取系统可用内存
     * @param context
     * @return
     */
    public static long getAvailMem(Context context){
        ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo=new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        long availMem=outInfo.availMem;//单位byte
        return availMem;
    }



    /**
     * 分别获取用户进程、系统进程
     * @param context
     * @param userTasks
     * @param systemTasks
     */
    public static void getTaskInfos(Context context,List<TaskInfo> userTasks,List<TaskInfo> systemTasks){
        PackageManager packageManager=context.getPackageManager();
        ActivityManager activityManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ProgressManager.Process> runningProcesses=ProgressManager.getRunningProcesses();//获取运行进程
        for(ProgressManager.Process process:runningProcesses){
            if(!process.getPackageName().equals(context.getPackageName())){
                TaskInfo taskInfo = new TaskInfo();//任务
                String packageName=process.getPackageName();
                taskInfo.setPackageName(packageName);
                try {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);//依据进程名字获取应用信息
                    Drawable taskIcon = applicationInfo.loadIcon(packageManager);//图标
                    if (taskIcon == null) {
                        taskInfo.setTaskIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
                    } else {
                        taskInfo.setTaskIcon(taskIcon);
                    }
                    taskInfo.setTaskName(applicationInfo.loadLabel(packageManager).toString());//应用名字
                    taskInfo.setId(process.pid);//进程id
                    Debug.MemoryInfo[] processMemryInfo = activityManager.getProcessMemoryInfo(new int[]{process.pid});//依据进程id获取占用内存
                    Debug.MemoryInfo memoryInfo = processMemryInfo[0];
                    long totalPrivateDirty = memoryInfo.getTotalPrivateDirty();//KB
                    taskInfo.setTaskMemeroy(totalPrivateDirty);
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {//用户进程
                        userTasks.add(taskInfo);
                    } else {
                        systemTasks.add(taskInfo);//系统进程
                    }
                } catch (PackageManager.NameNotFoundException e) {
                   // e.printStackTrace();


                }

            }
        }
       /* List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos=activityManager.getRunningAppProcesses();//依据活动管理获得正运行的进程
        List<ActivityManager.RunningTaskInfo> runningTaskInfos=activityManager.getRunningTasks(100);
        Toast.makeText(context,runningTaskInfos.size(),Toast.LENGTH_SHORT).show();

        for(ActivityManager.RunningAppProcessInfo info:runningAppProcessInfos) {//遍历正在运行的进程
            if (!info.processName.equals(context.getPackageName())) {//获取出本应用外其它应用
                TaskInfo taskInfo = new TaskInfo();//任务
                String packageName = info.processName;//进程名字 、包名
                taskInfo.setPackageName(packageName);
                try {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);//依据进程名字获取应用信息
                    Drawable taskIcon = applicationInfo.loadIcon(packageManager);//图标
                    if (taskIcon == null) {
                        taskInfo.setTaskIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
                    } else {
                        taskInfo.setTaskIcon(taskIcon);
                    }
                    taskInfo.setTaskName(applicationInfo.loadLabel(packageManager).toString());//应用名字
                    taskInfo.setId(info.pid);//进程id
                    Debug.MemoryInfo[] processMemryInfo = activityManager.getProcessMemoryInfo(new int[]{info.pid});//依据进程id获取占用内存
                    Debug.MemoryInfo memoryInfo = processMemryInfo[0];
                    long totalPrivateDirty = memoryInfo.getTotalPrivateDirty();//KB
                    taskInfo.setTaskMemeroy(totalPrivateDirty);
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {//用户进程
                        userTasks.add(taskInfo);
                    } else {
                        systemTasks.add(taskInfo);//系统进程
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }*/
    }


    /**
     * 锁频关闭所有其它用户进程
     * @param context
     */
    public static void CloseAllTasks(Context context){
        PackageManager packageManager=context.getPackageManager();
        ActivityManager activityManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos=activityManager.getRunningAppProcesses();//依据活动管理获得正运行的进程
        for(ActivityManager.RunningAppProcessInfo runningAppProcessInfo:runningAppProcessInfos){
            if(!runningAppProcessInfo.processName.equals(context.getPackageName())){
                try{
                    ApplicationInfo applicationInfo=packageManager.getApplicationInfo(runningAppProcessInfo.processName,0);//依据进程名字获取应用信息
                    if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==0){//不是系统进程
                        //Process.killProcess(runningAppProcessInfo.pid);//关掉进程
                        activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);

                    }
                }catch (PackageManager.NameNotFoundException e){
                   // e.printStackTrace();
                }
            }
        }
    }


    /**
     * 反射调用关闭所有进程
     * @param context
     */
    public static void CloseTasks(Context context){
        PackageManager packageManager=context.getPackageManager();
        ActivityManager activityManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos=activityManager.getRunningAppProcesses();//依据活动管理获得正运行的进程
        for(ActivityManager.RunningAppProcessInfo runningAppProcessInfo:runningAppProcessInfos) {
            if (!runningAppProcessInfo.processName.equals(context.getPackageName())) {
                try {
                    ApplicationInfo applicationInfo=packageManager.getApplicationInfo(runningAppProcessInfo.processName,0);//依据进程名字获取应用信息
                    if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==0) {//不是系统进程
                        //Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
                       // method.invoke(activityManager,runningAppProcessInfo.processName);
                        kill(runningAppProcessInfo.processName);//依据包名锁频关闭所有进程
                    }
                } catch (PackageManager.NameNotFoundException e) {
                   // e.printStackTrace();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 关闭选中进程
     * @param taskInfo
     */
    public static void CloseSelectedTask(Context context,TaskInfo taskInfo){
       // Process.killProcess(taskInfo.getId());//关闭进程
       //ActivityManager activityManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        //activityManager.killBackgroundProcesses(taskInfo.getPackageName());
       // try{
       //     Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage",String.class);
       //     method.invoke(activityManager,taskInfo.getPackageName());
        //    Toast.makeText(context,"关闭进程 :"+taskInfo.getPackageName(),Toast.LENGTH_SHORT).show();
     //   }catch (Exception e){

     //   }
        kill(taskInfo.getPackageName());

    }


    /**
     * Root权限关闭进程
     * @param packageName
     */
    public static void kill(String packageName){
        initProcess();
        killProcess(packageName);
        close();
    }

    /**
     * 初始化进程
     */
    private static void initProcess() {
        if (process == null)
            try {
                process = Runtime.getRuntime().exec("su");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * 结束进程
     * @param packageName
     */
    public static void killProcess(String packageName){
        OutputStream out = process.getOutputStream();
        String cmd = "am force-stop " + packageName + " \n";
        try {
            out.write(cmd.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 关闭输出流
     */
    private static void close() {
        if (process != null)
            try {
                process.getOutputStream().close();
                process = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }



    /**
     * 计算已使用de内存，并返回
     * @param context
     * @return
     */
    public static int getUsedValue(Context context) {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr,2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
            long availableSize = MyWindowManager.getAvailableMemory(context) / 1024;
            int usedMemorySize=(int)(totalMemorySize - availableSize);
            //int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
            //return percent + "%";
            return usedMemorySize;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 计算总内存，并返回
     * @param context
     * @return
     */
    public static int getAllValue(Context context) {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr,2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+", ""));
            //long availableSize = MyWindowManager.getAvailableMemory(context) / 1024;
            //int usedMemorySize=(int)(totalMemorySize - availableSize);
            //int percent = (int) ((totalMemorySize - availableSize) / (float) totalMemorySize * 100);
            //return percent + "%";
            return new Long(totalMemorySize).intValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }









}
