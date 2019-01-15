package com.example.slipwindow;
/**
 * App设置
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.example.slipwindow.Adapter.AppAdapter;
import com.example.slipwindow.View.SelectPicPopupWnidow;
import com.example.slipwindow.packageStatsObserver.DataCleanManager;
import com.example.slipwindow.service.FloatWindowService;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.moments.WechatMoments;

public class SettingActivity extends AppCompatActivity {
    private Switch susWindowSwitch;//悬浮窗
    private boolean susSelected;//悬浮选择
    private Button shareAppButton;
    private SelectPicPopupWnidow selectPicPopupWnidow;
    private static String TEST_IMAGE;
    private String appCache1;
    private Button aboutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("设置");
        setContentView(R.layout.activity_setting);
        ShareSDK.initSDK(this);
        initImage();//初始化分享的本地图片
        shareAppButton=(Button)findViewById(R.id.share_app_button);
       // clearButton=(Button)findViewById(R.id.this_app_cache_clear_button);
       // textView=(TextView)findViewById(R.id.this_app_cache_textview);//显示缓存
        aboutButton=(Button)findViewById(R.id.about_app_button);
       // new Thread(runable).start();
       // setAppCache();//初始化缓存大小
        // textView.setText(appCache1);
      /*  clearButton.setOnClickListener(new View.OnClickListener() {//清空缓存
            @Override
            public void onClick(View v) {
                DataCleanManager.cleanInternalCache(SettingActivity.this);//清理内部缓存
                DataCleanManager.cleanExternalCache(SettingActivity.this);//清理外部缓存
              //  setAppCache();//改变缓存大小
                textView.setText(appCache1);
            }
        });*/

        shareAppButton.setOnClickListener(new View.OnClickListener() {//分享该应用
            @Override
            public void onClick(View v) {
                showShare();
           }
       });

      aboutButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent=new Intent(SettingActivity.this,AboutAppActivity.class);
                startActivity(intent);
            }
        });

        final SharedPreferences pre=getSharedPreferences("phoneModle",MODE_PRIVATE);
        susSelected=pre.getBoolean("susSelected",false);
        susWindowSwitch=(Switch)findViewById(R.id.float_switch);
        susWindowSwitch.setChecked(susSelected);
        susWindowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){//选择悬浮窗
                    SharedPreferences.Editor editor=pre.edit();
                    editor.putBoolean("susSelected",true);
                    editor.apply();
                    Intent intent=new Intent(SettingActivity.this, FloatWindowService.class);
                    startService(intent);
                }else{//关闭悬浮窗
                    SharedPreferences.Editor editor=pre.edit();
                    editor.putBoolean("susSelected",false);
                    editor.apply();
                    Intent intent=new Intent(SettingActivity.this,FloatWindowService.class);
                    stopService(intent);

                }

            }
        });
    }


    /*final Runnable runable=new Runnable() {
        @Override
        public void run() {
            setAppCache();//初始化缓存大小
            myHandler.obtainMessage().sendToTarget();

        }
    };
    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            textView.setText(appCache1);
            clearButton.setOnClickListener(new View.OnClickListener() {//清空缓存
                @Override
                public void onClick(View v) {
                    DataCleanManager.cleanInternalCache(SettingActivity.this);//清理内部缓存
                    DataCleanManager.cleanExternalCache(SettingActivity.this);//清理外部缓存
                    setAppCache();//改变缓存大小
                    textView.setText(appCache1);
                }
            });

            shareAppButton.setOnClickListener(new View.OnClickListener() {//分享该应用
                @Override
                public void onClick(View v) {
                    showShare();
                }
            });

            aboutButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Intent intent=new Intent(SettingActivity.this,AboutAppActivity.class);
                    startActivity(intent);
                }
            });

            final SharedPreferences pre=getSharedPreferences("phoneModle",MODE_PRIVATE);
            susSelected=pre.getBoolean("susSelected",false);
            susWindowSwitch=(Switch)findViewById(R.id.float_switch);
            susWindowSwitch.setChecked(susSelected);
            susWindowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){//选择悬浮窗
                        SharedPreferences.Editor editor=pre.edit();
                        editor.putBoolean("susSelected",true);
                        editor.apply();
                        Intent intent=new Intent(SettingActivity.this, FloatWindowService.class);
                        startService(intent);
                    }else{//关闭悬浮窗
                        SharedPreferences.Editor editor=pre.edit();
                        editor.putBoolean("susSelected",false);
                        editor.apply();
                        Intent intent=new Intent(SettingActivity.this,FloatWindowService.class);
                        stopService(intent);

                    }

                }
            });
        }

    };*/


    /**
     * 分享到新浪、微信、qq等，不同平台共用此函数，会自动调用需要的定义属性函数
     */
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.addHiddenPlatform(TencentWeibo.NAME);//隐藏腾讯微博图标
        oks.addHiddenPlatform(WechatMoments.NAME);//隐藏微信朋友圈图标
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
          oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
          oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("一款不错的手机管家“神盾2017”,喜欢就分享喽~");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        //  oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(SettingActivity.TEST_IMAGE);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
         oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        // oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        //  oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
           oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }




    /**
     * 获取本地图片
     */
    private void initImage() {
        try {// 判断SD卡中是否存在此文件夹
            if (Environment.MEDIA_MOUNTED.equals(Environment
                    .getExternalStorageState())
                    && Environment.getExternalStorageDirectory().exists()) {
                File baseFile = new File(
                        Environment.getExternalStorageDirectory(), "share");
                if (!baseFile.exists()) {
                    baseFile.mkdir();
                }
                TEST_IMAGE = baseFile.getAbsolutePath() + "/app1_icon.png";
            } else {
                TEST_IMAGE = getApplication().getFilesDir().getAbsolutePath()
                        + "/app1_icon.png";
            }
            File file = new File(TEST_IMAGE);
            // 判断图片是否存此文件夹中
            if (!file.exists()) {
                file.createNewFile();
                Bitmap pic = BitmapFactory.decodeResource(getResources(),
                        R.drawable.app1_icon);
                FileOutputStream fos = new FileOutputStream(file);
                pic.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            TEST_IMAGE = null;
        }
    }


    /**
     * 设置缓存大小
     */
    public void setAppCache(){
        try{
            getAppSize(SettingActivity.this,getPackageName());//获取缓存大小
        }catch (Exception e){

        }
    }

    /**
     * 获取应用大小
     * @param context
     * @param pkgName
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void getAppSize(final Context context, String pkgName)
            throws NoSuchMethodException,InvocationTargetException,IllegalAccessException{
        Method method= PackageManager.class.getMethod("getPackageSizeInfo",String.class, IPackageStatsObserver.class);
        method.invoke(context.getPackageManager(),pkgName,new IPackageStatsObserver.Stub(){
            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
               // appSize1= Formatter.formatFileSize(context,pStats.codeSize);
                appCache1=Formatter.formatFileSize(context,pStats.cacheSize);
              //  appData1=Formatter.formatFileSize(context,pStats.dataSize);
            }
        });

    }

    private View.OnClickListener itemsOnClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectPicPopupWnidow.dismiss();
            switch (v.getId()){

                default:
                    break;
            }
        }
    };


    public void onDestroy(){
        super.onDestroy();
        ShareSDK.stopSDK(this);//停止shareSDK
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
