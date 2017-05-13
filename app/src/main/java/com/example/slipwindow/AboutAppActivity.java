package com.example.slipwindow;
/**
 * 本APP详细信息
 */

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.slipwindow.packageStatsObserver.AppSize;

import java.lang.reflect.Method;

public class AboutAppActivity extends AppCompatActivity {

    private String appName;
    private String appVersion;
    private PackageManager pm;
    private PackageInfo packageInfo;
    private Drawable appDrawable;
    private TextView appNameText;
    private  TextView appVersionText;
    private  TextView appSizeText;
    private ImageView appImageView;
    private ListView appPower;
    private String[] appermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("关于");
        setContentView(R.layout.activity_about_app);
        pm=getPackageManager();
        String name="名称：";
        String version="版本：";
       try{
           packageInfo=pm.getPackageInfo(getApplication().getPackageName(),PackageManager.GET_ACTIVITIES|PackageManager.GET_PERMISSIONS);
           ApplicationInfo applicationInfo=pm.getApplicationInfo(getApplication().getPackageName(),0);
           appName=applicationInfo.loadLabel(pm).toString();
           appDrawable=applicationInfo.loadIcon(pm);
           appVersion=packageInfo.versionName;
           appermission =packageInfo.requestedPermissions;//请求的权限
           appNameText=(TextView)findViewById(R.id.about_app_name);//名称
           appVersionText=(TextView)findViewById(R.id.about_app_version);//版本
           appSizeText=(TextView)findViewById(R.id.about_app_size_textview);//应用大小
           appImageView=(ImageView)findViewById(R.id.about_app_imageview);
           appPower=(ListView)findViewById(R.id.about_app_power_list);
           appNameText.setText(name+appName);
           appVersionText.setText(version+appVersion);
           appImageView.setImageDrawable(appDrawable);
           ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(AboutAppActivity.this,android.R.layout.simple_expandable_list_item_1,appermission);
           appPower.setAdapter(arrayAdapter);
           /**
            * 获得应用大小
            */
           AppSize.getAppSize(AboutAppActivity.this,getApplication().getPackageName(),appSizeText);
        }catch(Exception e){
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
