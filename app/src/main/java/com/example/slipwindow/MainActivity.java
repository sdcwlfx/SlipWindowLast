package com.example.slipwindow;
/**
 * 主界面
 */

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.slipwindow.service.FloatWindowService;
import com.example.slipwindow.service.FlowManageService;
import com.example.slipwindow.util.TasksUtil;
import com.zjun.progressbar.CircleDotProgressBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{
    private long firstTime=0;
    private CircleDotProgressBar circleDotProgressBar;
    private DrawerLayout drawerLayout;
    private View subView;//子布局
    private ViewPager viewPager;
    private ArrayList<View> pageView;
    private Button taskButton;
    private Button trashButton;
    private ImageView scrollbar;//滚动条图片
    private int offset = 0;//滚动条初始偏移量
    private int currIndex = 0;//当前页编号
    private int bmpW;//滚动条宽度
    private int one;//一倍滚动量



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //View view=findViewById(R.id.app_bar_main);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        LayoutInflater inflater =getLayoutInflater();
        View view1 = inflater.inflate(R.layout.main_task_close, null);
        View view2 = inflater.inflate(R.layout.main_trash_clean,null);
        taskButton = (Button) findViewById(R.id.main_task_close_button);
        trashButton = (Button)findViewById(R.id.main_trash_clean_button);
        taskButton.setOnClickListener(this);
        trashButton.setOnClickListener(this);
        scrollbar = (ImageView)findViewById(R.id.scrollbar);
        pageView =new ArrayList<View>();
        pageView.add(view1);
        pageView.add(view2);

        //数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter(){

            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return pageView.size();
            }

            @Override
            //判断是否由对象生成界面
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0==arg1;
            }
            //使从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(pageView.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1){
                ((ViewPager)arg0).addView(pageView.get(arg1));
                return pageView.get(arg1);
            }
        };
        //绑定适配器
        viewPager.setAdapter(mPagerAdapter);
        //设置viewPager的初始界面为第一个界面
        viewPager.setCurrentItem(0);
        //添加切换界面的监听器
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        // 获取滚动条的宽度
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.scrollbar).getWidth();
        //为了获取屏幕宽度，新建一个DisplayMetrics对象
        DisplayMetrics displayMetrics = new DisplayMetrics();
        //将当前窗口的一些信息放在DisplayMetrics类中
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //得到屏幕的宽度
        int screenW = displayMetrics.widthPixels;
        //计算出滚动条初始的偏移量
        offset = (screenW / 2 - bmpW) / 2;
        //计算出切换一个界面时，滚动条的位移量
        one = offset * 2 + bmpW;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        //将滚动条的初始位置设置成与左边界间隔一个offset
        scrollbar.setImageMatrix(matrix);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        phoneModleSelect();//来电模式设定及开启FowManageService监听一天结束
        /**
         * 滑动窗口布局
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /**
         * 获取滑动窗口中控件，默认选中名单管理，并监听点击事件
         */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.list_manage);
        navigationView.setNavigationItemSelectedListener(this);
        String defaultSmsApp=null;
        String currentPn=getPackageName();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
        {
            defaultSmsApp= Telephony.Sms.getDefaultSmsPackage(this);//获取手机当前设置的默认短信应用包名
        }
        if(!defaultSmsApp.equals(currentPn))
        {
            Intent intent=new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,currentPn);
            startActivity(intent);
        }

       /* circleDotProgressBar.setProgressMax(TasksUtil.getAllValue(MainActivity.this));
        circleDotProgressBar.setProgress(TasksUtil.getUsedValue(MainActivity.this));
        circleDotProgressBar.setOnButtonClickListener(new View.OnClickListener() {//一键加速
            @Override
            public void onClick(View v) {//关闭进程

                circleDotProgressBar.setProgress(TasksUtil.getUsedValue(MainActivity.this));
            }
        });*/
    }


    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    /**
                     * TranslateAnimation的四个属性分别为
                     * float fromXDelta 动画开始的点离当前View X坐标上的差值
                     * float toXDelta 动画结束的点离当前View X坐标上的差值
                     * float fromYDelta 动画开始的点离当前View Y坐标上的差值
                     * float toYDelta 动画开始的点离当前View Y坐标上的差值
                     **/
                    animation = new TranslateAnimation(one, 0, 0, 0);
                    break;
                case 1:
                    animation = new TranslateAnimation(offset, one, 0, 0);
                    break;
            }
            //arg0为切换到的页的编码
            currIndex = arg0;
            // 将此属性设置为true可以使得图片停在动画结束时的位置
            animation.setFillAfter(true);
            //动画持续时间，单位为毫秒
            animation.setDuration(200);
            //滚动条开始动画
            scrollbar.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.main_task_close_button:
                //点击""进程关闭“时切换到第一页
                viewPager.setCurrentItem(0);
                circleDotProgressBar=(CircleDotProgressBar)findViewById(R.id.bar_all);
                circleDotProgressBar.setProgressMax(TasksUtil.getAllValue(MainActivity.this));
                circleDotProgressBar.setProgress(TasksUtil.getUsedValue(MainActivity.this));
                circleDotProgressBar.setOnButtonClickListener(new View.OnClickListener() {//一键加速
                    @Override
                    public void onClick(View v) {//关闭进程

                        circleDotProgressBar.setProgress(TasksUtil.getUsedValue(MainActivity.this));
                    }
                });
                break;
            case R.id.main_trash_clean_button:
                //点击“清理”时切换的第二页
                viewPager.setCurrentItem(1);
                break;
        }
    }


    /**
     * 返回按钮动作
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 菜单栏加载
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * 菜单栏点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
          //  Intent intent=new Intent(MainActivity.this,SettingActivity.class);
            Intent intent=new Intent(MainActivity.this,TestActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 左侧滑动窗口点击事件
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.list_manage) {//名单管理
            Intent intent=new Intent(MainActivity.this,ListManageActivity.class);
            startActivity(intent);
        } else if (id == R.id.soft_manage) {//软件管理
            Intent intent=new Intent(MainActivity.this,SoftManageActivity.class);
            startActivity(intent);

        } else if (id == R.id.task_manage) {//任务管理
            Intent intent=new Intent(MainActivity.this,TasksManageActivity.class);
            startActivity(intent);

        } else if (id == R.id.harass_intercept) {//骚扰拦截
            Intent intent=new Intent(MainActivity.this,HarrassManageActivity.class);
            startActivity(intent);
        } else if (id == R.id.flow_manage) {//流量管理
            Intent intent=new Intent(MainActivity.this,FlowManageActivity.class);
            startActivity(intent);

        } else if (id == R.id.net_control) {//上网监控
            Intent intent=new Intent(MainActivity.this,NetControlActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);//关闭滑动菜单
        return true;
    }

    /**
     * 来电短信拦截模式设定
     */
    public void phoneModleSelect(){
        SharedPreferences pre=getSharedPreferences("phoneModle",MODE_PRIVATE);

        if(pre.getBoolean("first",true)){
            SharedPreferences.Editor editor=pre.edit();
            editor.putInt("select",1);//1：标准 2：全开 3：全拦 4：拦黑名单和开白名单 5：情景模式
            editor.putBoolean("first",false);
            editor.apply();
        }
        if(pre.getBoolean("firstMessage",true)){
            SharedPreferences.Editor editor=pre.edit();
            editor.putInt("selectMessage",2);
            editor.putBoolean("firstMessage",false);
            editor.apply();
        }
        if(!pre.getBoolean("hasTaskSetting",false)){
            SharedPreferences.Editor editor=pre.edit();
            editor.putBoolean("taskSetting",false);
            editor.putBoolean("hasTaskSetting",true);
            editor.apply();
        }
        if(pre.getBoolean("susSelected",false)){//悬浮窗
            Intent intent=new Intent(MainActivity.this, FloatWindowService.class);
            startService(intent);
        }
        if(!pre.getBoolean("flowManage",false)){
            SharedPreferences.Editor editor=pre.edit();
            editor.putBoolean("flowManage",true);
            editor.apply();
        }
        String myFlow=pre.getString("myFlow","未接受到自定义广播");
        String close=pre.getString("phoneClose","未接受到关机广播");
        Toast.makeText(MainActivity.this,myFlow+close,Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this, FlowManageService.class);
        startService(intent);
    }

    /**
     * 两次按键退出应用
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyUp(int keyCode, KeyEvent event){
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                long secondTime=System.currentTimeMillis();//当前时间
                if(secondTime-firstTime>2000)//两秒
                {
                    Toast.makeText(MainActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                    firstTime=secondTime;
                    return true;
                }else{//两次按键小于2秒
                    //System.exit(0);//退出程序
                    //ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    //am.killBackgroundProcesses(getPackageName());
                    //Process.(Process.myPid());
                   // Process.killProcess(Process.myPid());
                    finish();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

}
