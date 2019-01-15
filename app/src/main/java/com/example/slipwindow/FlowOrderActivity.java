package com.example.slipwindow;
/**
 * 流量排行使用状况
 */

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.slipwindow.Adapter.AppAdapter;
import com.example.slipwindow.Adapter.FlowOrderAdapter;
import com.example.slipwindow.entity.FlowAppUsedSituation;
import com.example.slipwindow.util.FlowStorageManage;
import com.example.slipwindow.util.TasksUtil;
import com.zjun.progressbar.CircleDotProgressBar;

import java.util.ArrayList;

public class FlowOrderActivity extends AppCompatActivity implements View.OnClickListener{
    private ViewPager viewPager;
    private ArrayList<View> pageView;
    private ListView todayListView;
    private ListView monthListView;
    private Button dayButton;
    private Button monthButton;
    private ArrayList<FlowAppUsedSituation> todayFlowAppUsedSituations;
    private ArrayList<FlowAppUsedSituation> monthFlowAppUsedSituations;
    private ProgressBar progressBar;
    private FlowOrderAdapter todayFlowOrderAdapter;
    private FlowOrderAdapter monthFlowOrderAdapter;
    private  View view1;
    private  View view2;
    private ImageView imageView;
    private int offset = 0;//滚动条初始偏移量
    private int currIndex = 0;//当前页编号
    private int bmpW;//滚动条宽度
    private int one;//一倍滚动量
    private TextView flowTodayTextView;
    private TextView flowMonthTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("流量排行");
        setContentView(R.layout.activity_flow_order);
        imageView=(ImageView)findViewById(R.id.flow_order_image_view);
        dayButton=(Button)findViewById(R.id.flow_order_today_button);
        monthButton=(Button)findViewById(R.id.flow_order_month_button);
        viewPager=(ViewPager)findViewById(R.id.flow_order_viewPager);
        progressBar=(ProgressBar)findViewById(R.id.flow_order_progress_bar);
        LayoutInflater inflater =getLayoutInflater();
        view1 = inflater.inflate(R.layout.flow_order_today, null);//今日排行布局
        view2 = inflater.inflate(R.layout.flow_order_month,null);//本月排行布局
        flowTodayTextView=(TextView) view1.findViewById(R.id.flow_order_today_list_state_view);
        flowMonthTextView=(TextView)view2.findViewById(R.id.flow_order_month_list_state_view);
        todayListView=(ListView)view1.findViewById(R.id.flow_order_day_list_view);
        monthListView=(ListView)view2.findViewById(R.id.flow_order_month_list_view);
        dayButton.setOnClickListener(this);
        monthButton.setOnClickListener(this);
        pageView =new ArrayList<View>();
        new Thread(runable).start();//开启线程获取流量使用数据
    }

    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            todayFlowAppUsedSituations= FlowStorageManage.getTodyUsedMobile(FlowOrderActivity.this);//获取今日应用流量使用情况
            monthFlowAppUsedSituations=FlowStorageManage.getLastMonth(FlowOrderActivity.this);//获取最近30天各应用流量使用情况
           /* for(int i=0;i<monthFlowAppUsedSituations.size()-1;i++){
                for(int j=0;j<monthFlowAppUsedSituations.size()-i-j;j++){
                    if(monthFlowAppUsedSituations.get(j).getUsedMobile()<monthFlowAppUsedSituations.get(j+1).getUsedMobile())
                }
            }*/
            myHandler.obtainMessage().sendToTarget();

        }
    };
    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            progressBar.setVisibility(View.GONE);//进度条不可见
            todayFlowOrderAdapter=new FlowOrderAdapter(FlowOrderActivity.this,todayFlowAppUsedSituations);
            todayListView.setAdapter(todayFlowOrderAdapter);
            monthFlowOrderAdapter=new FlowOrderAdapter(FlowOrderActivity.this,monthFlowAppUsedSituations);
            monthListView.setAdapter(monthFlowOrderAdapter);
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
            viewPager.addOnPageChangeListener(new FlowOrderActivity.MyOnPageChangeListener());
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
            imageView.setImageMatrix(matrix);


         //   viewPager.setCurrentItem(0);//默认显示第一页
        }
    };


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
            imageView.startAnimation(animation);
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
            case R.id.flow_order_today_button:
                //点击"今日流量"时切换到第一页
                if(todayFlowAppUsedSituations.size()==0){
                    flowTodayTextView.setVisibility(View.VISIBLE);
                }
                viewPager.setCurrentItem(0);
                break;
            case R.id.flow_order_month_button:
                //点击“最近三十天流量”时切换的第二页
                if(monthFlowAppUsedSituations.size()==0){
                    flowMonthTextView.setVisibility(View.VISIBLE);
                }
                viewPager.setCurrentItem(1);
                break;
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
