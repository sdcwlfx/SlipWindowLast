package com.example.slipwindow;
/**
 * 上网监控
 */

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.slipwindow.Adapter.AppAdapter;
import com.example.slipwindow.Adapter.NetControlAdapter;
import com.example.slipwindow.entity.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NetControlActivity extends AppCompatActivity {
    PackageManager packageManager;
    List<AppInfo> appInfoList;
    RecyclerView netAppView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("上网监控");
        setContentView(R.layout.activity_net_control);
        netAppView=(RecyclerView)findViewById(R.id.net_app_list_view);
        progressBar=(ProgressBar)findViewById(R.id.net_progress_bar);
        new Thread(runable).start();
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
            progressBar.setVisibility(View.GONE);
            NetControlAdapter netControlAdapter=new NetControlAdapter(appInfoList,NetControlActivity.this);
            netAppView.setAdapter(netControlAdapter);

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
                try{
                    ApplicationInfo applicationInfo=packageManager.getApplicationInfo(info.activityInfo.packageName,0);//依据进程名字获取应用信息
                    if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==0){//不是系统应用
                        appInfo.setAppNmae(info.loadLabel(packageManager).toString());
                        appInfo.setIcon(info.loadIcon(packageManager));//获取图标
                        appInfo.setAppPackageName(info.activityInfo.packageName);
                        //为应用启动Activity准备Intent
               /* Intent launchIntent=new Intent();
                launchIntent.setComponent(new ComponentName(info.activityInfo.packageName,info.activityInfo.name));
                appInfo.setIntent(launchIntent);*/
                        appInfoList.add(appInfo);//添加到列表中
                    }

                }catch (Exception e){
                }
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
}
