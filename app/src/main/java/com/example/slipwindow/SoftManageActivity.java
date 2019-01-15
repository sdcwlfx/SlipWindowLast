package com.example.slipwindow;
/**
 * 软件管理
 */

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.slipwindow.Adapter.AppAdapter;
import com.example.slipwindow.entity.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogRecord;

public class SoftManageActivity extends AppCompatActivity {
    private ListView listView=null;
    private ArrayList<AppInfo> appInfoList=null;
    private PackageManager packageManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("软件管理");
        setContentView(R.layout.activity_soft_manage);
        packageManager=getPackageManager();
        listView=(ListView)findViewById(R.id.app_list_view);
        new Thread(runable).start();//开新线程加载应用列表
        progressBar=(ProgressBar)findViewById(R.id.soft_manage_progress_bar);
       // Intent intent=new Intent("com.exmple.slipwindow.TODAY_USED_MOBILE");
       // sendOrderedBroadcast(intent,null);
    }

    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            loadApplications();//加载应用
            myHandler.obtainMessage().sendToTarget();

        }
    };
    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
              AppAdapter appAdapter=new AppAdapter(SoftManageActivity.this,appInfoList);
            progressBar.setVisibility(View.GONE);//进度条不可见
             listView.setAdapter(appAdapter);//与listView控件绑定
            listView.setOnItemClickListener(new AppListItemLinster());//监听
        }

    };

    /**
     * 加载应用列表
     */
    public void loadApplications(){
        packageManager=getPackageManager();
        Intent mainIntent=new Intent(Intent.ACTION_MAIN,null);//限制条件:有主活动
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);//限制条件：有应用图标
        final List<ResolveInfo> apps=packageManager.queryIntentActivities(mainIntent,0);//加载符合条件的应用（活动）
        Collections.sort(apps,new ResolveInfo.DisplayNameComparator(packageManager));//调用系统排序，根据name排序
        if(apps!=null){
            final int count=apps.size();//要显示的应用数目
            if(appInfoList==null){
                appInfoList=new ArrayList<AppInfo>(count);
            }
            appInfoList.clear();
            for(int i=0;i<count;i++){
                AppInfo appInfo=new AppInfo();
                ResolveInfo info=apps.get(i);
                appInfo.setAppNmae(info.loadLabel(packageManager).toString());
                appInfo.setIcon(info.loadIcon(packageManager));//获取图标
                appInfo.setAppPackageName(info.activityInfo.packageName);
                //为应用启动Activity准备Intent
                Intent launchIntent=new Intent();
                launchIntent.setComponent(new ComponentName(info.activityInfo.packageName,info.activityInfo.name));
                appInfo.setIntent(launchIntent);
                appInfoList.add(appInfo);//添加到列表中

            }
        }
    }



    /**
     * 标题栏返回按钮
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
     * 列表监听类，可弹出提示框：启动、卸载、查看详细信息
     */
    public final class AppListItemLinster implements AdapterView.OnItemClickListener{
        AlertDialog dialog;

         //根据应用所在包名获取该包所有活动，并返回主活动名
        public String getActivityName(String packName) throws Exception {
            final PackageInfo packageInfo=packageManager.getPackageInfo(packName,PackageManager.GET_ACTIVITIES);
            final ActivityInfo[] activityInfos=packageInfo.activities;
            if(activityInfos==null||activityInfos.length<=0)
            {
                return null;
            }
            return activityInfos[0].name;
        }


         //据包名获取应用信息（活动、权限）
        public PackageInfo getAppPackInfo(String packName) throws Exception{
            return packageManager.getPackageInfo(packName,PackageManager.GET_ACTIVITIES|PackageManager.GET_PERMISSIONS);
        }


         //利用包名和程序活动开启应用
        public void startApp(AppInfo appInfo) throws Exception{
            final String packName=appInfo.getAppPackageName();//获取指定应用包名
            final String activityName=getActivityName(packName);//据包名获取应用主活动名
            if(activityName==null){
                Toast.makeText(SoftManageActivity.this,"程序启动失败",Toast.LENGTH_SHORT).show();
                return;
            }else {
            //Intent intent=new Intent();
           // intent.setComponent(new ComponentName(packName,activityName));//开启其它应用的活动，跨应用开启活动
           // startActivity(intent);
                startActivity(appInfo.getIntent());
            }
        }


        /**
         * 显示应用详细信息,调到ShowAppDetailActivity界面
         * @param appInfo
         */
        public void showAppDetail(AppInfo appInfo) throws Exception{
            final String packName=appInfo.getAppPackageName();
            final PackageInfo packageInfo=getAppPackInfo(packName);//据应用包名获取应用活动及权限
            final String versionName=packageInfo.versionName;//版本
            final String[] appremission=packageInfo.requestedPermissions;//请求的权限
            final String appName=appInfo.getAppNmae();
            Intent showAppDetailIntent=new Intent(SoftManageActivity.this,ShowAppDetailInfoActivity.class);
            Bundle bundle=new Bundle();//传递数据
            bundle.putString("packName",packName);
            bundle.putString("versionName",versionName);
            bundle.putString("appName",appName);
            bundle.putStringArray("appPermission",appremission);
            showAppDetailIntent.putExtras(bundle);
            startActivity(showAppDetailIntent);

        }

        /**
         * 据应用程序包名卸载软件
         */
        public void unstallApp(String packageName){
            Intent unstall_intent=new Intent();
            unstall_intent.setAction(Intent.ACTION_DELETE);//删除动作
            unstall_intent.setData(Uri.parse("package:"+packageName));
            //Toast.makeText(SoftManageActivity.this,"卸载",Toast.LENGTH_SHORT).show();
            startActivity(unstall_intent);
        }


        /**
         * 应用列表项的点击事件
         * @param view
         * @param arg1
         * @param position
         * @param arg3
         */
        public void onItemClick(AdapterView<?> view, View arg1, final int position, long arg3){
            AlertDialog.Builder builder=new AlertDialog.Builder(SoftManageActivity.this);
            builder.setTitle("选项");
            builder.setItems(R.array.array_choice, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final AppInfo appInfo=appInfoList.get(position);//选中应用
                    switch (which){
                        case 0://启动
                            try{
                                startApp(appInfo);

                            } catch(Exception e){

                            }
                            break;
                        case 1://卸载
                            try{
                                //子线程卸载后返回原活动界面
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        unstallApp(appInfo.getAppPackageName());
                                        appInfoList.remove(appInfo);
                                        myHandler.obtainMessage().sendToTarget();
                                    }
                                }).start();

                            } catch (Exception e){

                            }
                            break;
                        case 2://详细信息
                            try{
                                showAppDetail(appInfo);

                            } catch (Exception e){

                            }
                            break;
                    }
                    dialog.dismiss();//关闭提示框

                }


            });
            dialog=builder.create();
            dialog.show();
        }
    }

}
