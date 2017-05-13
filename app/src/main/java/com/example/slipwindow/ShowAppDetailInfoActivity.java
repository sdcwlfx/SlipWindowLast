package com.example.slipwindow;
/**
 * 选中应用详细信息
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;
import android.os.RemoteException;
import android.text.format.Formatter;

import java.io.File;
import java.lang.Runnable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.example.slipwindow.packageStatsObserver.DataCleanManager;
import com.example.slipwindow.util.Common;

public class ShowAppDetailInfoActivity extends AppCompatActivity {

    private String [] permissions;
    private String appName;
    private String appVersion;
    private String appSize;
    private String appCache;
    private ImageView appImage;
    private Drawable appDrawable;
    private TextView appNameView;
    private TextView appVersionView;
    private TextView appSizeView;
    private TextView appSizeView1;
    private TextView appCacheView;
    private TextView appCacheView1;
    private TextView appPowerView;
    private Button appCacheButton;
    private ListView appPowerList;
    private Bundle bundle;
    private PackageManager pm;
    private TextView appDataView;
    private TextView appDataView1;
    private Button appDataButton;
    private Context mContext;
    private String packageName;
    private static String appSize1;
    private static String appCache1;
    private static String appData1;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("应用信息");
        setContentView(R.layout.activity_show_app_detail_info);
        appImage=(ImageView)findViewById(R.id.app_detail_imageview);
        appNameView=(TextView)findViewById(R.id.app_detail_name);
        appDataView=(TextView)findViewById(R.id.app_detail_data_textview);
        appDataButton=(Button)findViewById(R.id.app_delete_data_button);
        appVersionView=(TextView)findViewById(R.id.app_detail_version);
        appSizeView=(TextView)findViewById(R.id.app_detail_size_textview);
        appCacheView=(TextView)findViewById(R.id.app_detail_cache_textview);
        appCacheButton=(Button) findViewById(R.id.app_delete_cache_button);
        appPowerList=(ListView)findViewById(R.id.app_power_list);
        appSizeView1=(TextView)findViewById(R.id.app_detail_size_view);
        appDataView1=(TextView)findViewById(R.id.app_detail_data_view);
        appCacheView1=(TextView)findViewById(R.id.app_detail_cache_view);
        appPowerView=(TextView)findViewById(R.id.app_detail_power_view);
        Intent intent=getIntent();
        bundle=intent.getExtras();
        permissions=bundle.getStringArray("appPermission");
        try{
            appName=bundle.getString("appName");
            appVersion=bundle.getString("versionName");
            packageName =bundle.getString("packName");
            pm=getPackageManager();
            ApplicationInfo applicationInfo=pm.getApplicationInfo(packageName,0);
            appDrawable=applicationInfo.loadIcon(pm);
            mContext=createPackageContext(packageName,CONTEXT_IGNORE_SECURITY);//目标应用上下文（忽略安全警告）
            new Thread(runable).start();
            progressBar=(ProgressBar)findViewById(R.id.app_detail_progress_bar);
            /**
             * 清理缓存
             */
            appCacheButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(Common.isRoot()){//含有root权限
                        if(packageName.equals("com.example.slipwindow")) {
                            DataCleanManager.cleanExternalCache(ShowAppDetailInfoActivity.this);
                            DataCleanManager.cleanInternalCache(ShowAppDetailInfoActivity.this);
                            new Thread(runable).start();
                            Toast.makeText(ShowAppDetailInfoActivity.this,"缓存清理成功",Toast.LENGTH_SHORT).show();
                        }else{
                            DataCleanManager.cleanOtherAppCache(mContext);
                            DataCleanManager.cleanOAppDerectory(mContext,mContext.getExternalCacheDir());
                            new Thread(runable).start();//更新界面缓存显示
                            Toast.makeText(ShowAppDetailInfoActivity.this,"其它应用缓存清理成功",Toast.LENGTH_SHORT).show();
                        }
                    }else{//无root权限
                        AlertDialog.Builder dialog=new AlertDialog.Builder(ShowAppDetailInfoActivity.this);
                        dialog.setTitle("提示").setMessage("请先获取Root权限");
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              //  finish();
                            }
                        });
                        dialog.show();

                    }
                }
            });
            /**
             * 清理数据
             */
            appDataButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(Common.isRoot()){
                        new AlertDialog.Builder(ShowAppDetailInfoActivity.this).setTitle("提示")
                                .setMessage("确定清空数据？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(packageName.equals("com.example.slipwindow")) {
                                            DataCleanManager.cleanApplicationData(ShowAppDetailInfoActivity.this);
                                            new Thread(runable).start();
                                            Toast.makeText(ShowAppDetailInfoActivity.this,"数据清理成功",Toast.LENGTH_SHORT).show();
                                        }else{
                                            DataCleanManager.cleanOtherAppData(mContext);
                                            new Thread(runable).start();
                                            Toast.makeText(ShowAppDetailInfoActivity.this,"其它应用数据清理成功",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).setNegativeButton("取消",null).show();
                    }else{
                        AlertDialog.Builder dialog=new AlertDialog.Builder(ShowAppDetailInfoActivity.this);
                        dialog.setTitle("提示").setMessage("请先获取Root权限");
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               // finish();
                            }
                        });
                        dialog.show();
                    }
                }

            });
        }catch (Exception e){

        }
    }

    /**
     * 返回箭头
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==android.R.id.home)
        {
            finish();//结束当前活动
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取应用大小
     * @param context
     * @param pkgName
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void getAppSize(final Context context, String pkgName)
            throws NoSuchMethodException,InvocationTargetException,IllegalAccessException{
        Method method= PackageManager.class.getMethod("getPackageSizeInfo",String.class, IPackageStatsObserver.class);
        method.invoke(context.getPackageManager(),pkgName,new IPackageStatsObserver.Stub(){
            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                appSize1= Formatter.formatFileSize(context,pStats.codeSize);
                appCache1=Formatter.formatFileSize(context,pStats.cacheSize);
                appData1=Formatter.formatFileSize(context,pStats.dataSize);
            }
        });

    }

    /**
     * 更新界面信息
     */
    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg) {
            if(progressBar.getVisibility()!= View.GONE){
                progressBar.setVisibility(View.GONE);//隐藏进度条
            }

            if(appDataButton.isPressed()){
                appDataView.setText(appData1);
                appCacheView.setText(appCache1);
            }else if(appCacheButton.isPressed()){
                appCacheView.setText(appCache1);
            }else{
                appSizeView1.setVisibility(View.VISIBLE);
                appDataView1.setVisibility(View.VISIBLE);
                appCacheView1.setVisibility(View.VISIBLE);
                appPowerView.setVisibility(View.VISIBLE);
                appDataButton.setVisibility(View.VISIBLE);
                appCacheButton.setVisibility(View.VISIBLE);
                appImage.setImageDrawable(appDrawable);
                appNameView.setText(appName);
                appVersionView.setText(appVersion);
                appSizeView.setText(appSize1);
                appCacheView.setText(appCache1);
                appDataView.setText(appData1);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ShowAppDetailInfoActivity.this, android.R.layout.simple_expandable_list_item_1, permissions);
                appPowerList.setAdapter(arrayAdapter);
            }

        }
    };

    /**
     * 新线程获取应用大小、缓存
     */
    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            try{

                getAppSize(mContext, packageName);
                Thread.sleep(2000);
                myHandler.obtainMessage().sendToTarget();
            }catch (Exception e){

            }
        }
    };


    /**
     * 判断是否有root权限，有返回true
     * @return
     */
  /*  public boolean isRoot() {
        boolean bool = false;
        try {
            bool = new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }*/


}
