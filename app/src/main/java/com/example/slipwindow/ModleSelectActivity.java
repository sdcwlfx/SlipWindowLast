package com.example.slipwindow;
/**
 * 来电模式选择：5种模式：标准、全开、全拦、仅黑白名单、情景模式
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.slipwindow.Adapter.SightTimeAdapter;
import com.example.slipwindow.db.SightModleTime;
import com.example.slipwindow.db.TimePhone;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.List;

public class ModleSelectActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton radioButton;//选中模式
    private FloatingActionButton floatingActionButton;//悬浮按钮
    private ListView sightListView;//情景模式时间段列表
    private List<SightModleTime> sightModleTimeList;//所有拦截时间段
    private AlertDialog alertDialog;
   // private ArrayList<String>
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("模式选择");
        setContentView(R.layout.activity_modle_select);
        radioGroup=(RadioGroup)findViewById(R.id.radio_group);//默认选择标准模式
        int check=getCheckedRadioButton();//获取选中模式
        setCheckedRadioButton(check,radioGroup);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.new_sight_button);//悬浮按钮
        sightListView=(ListView)findViewById(R.id.sight_list_view);//列表
        if(check==5)//情景模式
        {
            new Thread(runable).start();
            loadSightModleView();
            setClickItemLinsnerOnList();
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group,int checkedId){
                if(checkedId==R.id.radio_sight)//若是情景模式
                {
                    new Thread(runable).start();
                    loadSightModleView();//情景模式更新视图
                    setClickItemLinsnerOnList();
                    updateModleSelected(5);//更新选择的拦截模式

                }else if(checkedId==R.id.radio_normal){//标准模式
                    updateModleSelected(1);//更新选择的拦截模式
                    floatingActionButton.setVisibility(View.GONE);//按钮不可见
                    sightListView.setVisibility(View.GONE);//时间段列表不可见

                }else if(checkedId==R.id.radio_all_pass){//全开
                    updateModleSelected(2);//更新选择的拦截模式
                    floatingActionButton.setVisibility(View.GONE);//按钮不可见
                    sightListView.setVisibility(View.GONE);//时间段列表不可见
                }else if(checkedId==R.id.radio_all_harass){//全拦
                    updateModleSelected(3);//更新选择的拦截模式
                    floatingActionButton.setVisibility(View.GONE);//按钮不可见
                    sightListView.setVisibility(View.GONE);//时间段列表不可见
                }else if(checkedId==R.id.radio_whitepass_darkharass){//拦黑名单通过白名单
                    updateModleSelected(4);//更新选择的拦截模式
                    floatingActionButton.setVisibility(View.GONE);//按钮不可见
                    sightListView.setVisibility(View.GONE);//时间段列表不可见
                }

            }
        });





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
     * 获取选中的模式1、2、3、4、5、6
     * @return
     */
    public int getCheckedRadioButton(){
        SharedPreferences preferences=getSharedPreferences("phoneModle",MODE_PRIVATE);
        int checked=preferences.getInt("select",0);
        return checked;
    }

    /**
     * 设置选中的拦截模式
     * @param checked
     * @param radioGroup
     */
    public void setCheckedRadioButton(int checked,RadioGroup radioGroup){
        if(checked==1){
            radioGroup.check(R.id.radio_normal);//标准模式：仅拦黑名单
        }else if(checked==2){
            radioGroup.check(R.id.radio_all_pass);//全开
        }else if(checked==3){
            radioGroup.check(R.id.radio_all_harass);//全拦
        }else if(checked==4){
            radioGroup.check(R.id.radio_whitepass_darkharass);//拦黑名单过白名单
        }else if(checked==5){
            radioGroup.check(R.id.radio_sight);//情景模式
        }

    }

    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            sightModleTimeList=getSightModleTimeList();//加载所有时间段
            myHandler.obtainMessage().sendToTarget();

        }
    };

    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            loadSightTime(sightListView,ModleSelectActivity.this,sightModleTimeList);//加载适配器
        }

    };






    /**
     * 获取所有时间段对象
     * @return
     */
    public List<SightModleTime> getSightModleTimeList(){
        List<SightModleTime> sightModleTimeList= DataSupport.findAll(SightModleTime.class);//所有时间段对象
        return sightModleTimeList;
    }

    /**
     * 加载列表适配器
     * @param listView
     * @param context
     * @param sightModleTimes
     */
    public void loadSightTime(ListView listView, Context context,List<SightModleTime> sightModleTimes){
        SightTimeAdapter sightTimeAdapter=new SightTimeAdapter(context,sightModleTimes);//适配器
        listView.setAdapter(sightTimeAdapter);

    }

    /**
     * 更新选择的拦截模式
     * @param check
     */
    public void updateModleSelected(int check){
        SharedPreferences.Editor editor=getSharedPreferences("phoneModle",MODE_PRIVATE).edit();
        editor.putInt("select",check);
        editor.apply();
    }

    /**
     * 情景模式更新界面
     */
    public void loadSightModleView(){
        if(floatingActionButton.getVisibility()==View.GONE){
            floatingActionButton.setVisibility(View.VISIBLE);//按钮可见
            sightListView.setVisibility(View.VISIBLE);//时间段列表可见
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                View view=View.inflate(getApplicationContext(),R.layout.time_pickers,null);
               final TimePicker startTimePicker=(TimePicker)view.findViewById(R.id.start_time_picker);
                final TimePicker endTimePicker=(TimePicker)view.findViewById(R.id.end_time_picker);//结束时间
                int hour,minute;
                //默认当前时间
                final Calendar c=Calendar.getInstance();
                hour=c.get(Calendar.HOUR_OF_DAY);
                minute=c.get(Calendar.MINUTE);
                startTimePicker.setIs24HourView(true);
                startTimePicker.setCurrentHour(hour);
                startTimePicker.setCurrentMinute(minute);
                endTimePicker.setIs24HourView(true);
                endTimePicker.setCurrentHour(hour);
                endTimePicker.setCurrentMinute(minute);


                AlertDialog.Builder builder=new AlertDialog.Builder(ModleSelectActivity.this);
                builder.setView(view);
                builder.setTitle(R.string.select_time);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SightModleTime sightModleTime=new SightModleTime();
                        int startHour=startTimePicker.getCurrentHour();
                        int startMinute=startTimePicker.getCurrentMinute();
                        int endHour=endTimePicker.getCurrentHour();
                        int endMinute=endTimePicker.getCurrentMinute();
                        sightModleTime.setStartHourTime(startHour);
                        sightModleTime.setStartMinuteTime(startMinute);
                        sightModleTime.setEndHourTime(endHour);
                        sightModleTime.setEndMinuteTime(endMinute);
                        sightModleTime.setState("已开启");
                        sightModleTime.save();//保存到数据库
                        new Thread(runable).start();
                        Toast.makeText(ModleSelectActivity.this,"添加成功",Toast.LENGTH_SHORT).show();

                    }
                }).show();



            }
        });

    }



    /**
     * 列表项监听
     */
    public void setClickItemLinsnerOnList(){
        sightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(ModleSelectActivity.this);
                builder.setTitle("操作")
                        .setItems(R.array.array_time,new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                if(which==0){//启用
                                    SightModleTime sightModleTime1=sightModleTimeList.get(position);
                                    if(sightModleTime1.getState().equals("已开启")){
                                        Toast.makeText(ModleSelectActivity.this,"已开启",Toast.LENGTH_SHORT).show();
                                    }else{
                                        sightModleTime1.setState("已开启");
                                        sightModleTime1.save();
                                        new Thread(runable).start();
                                        Toast.makeText(ModleSelectActivity.this,"开启成功",Toast.LENGTH_SHORT).show();
                                    }
                                }else if(which==1){//关闭
                                    SightModleTime sightModleTime1=sightModleTimeList.get(position);
                                    if(sightModleTime1.getState().equals("已关闭")){
                                        Toast.makeText(ModleSelectActivity.this,"已关闭",Toast.LENGTH_SHORT).show();
                                    }else{
                                        sightModleTime1.setState("已关闭");
                                        sightModleTime1.save();
                                        new Thread(runable).start();
                                        Toast.makeText(ModleSelectActivity.this,"关闭成功",Toast.LENGTH_SHORT).show();
                                    }

                                }else if(which==2){//删除
                                    SightModleTime sightModleTime1=sightModleTimeList.get(position);
                                    sightModleTime1.delete();
                                    new Thread(runable).start();
                                    Toast.makeText(ModleSelectActivity.this,"删除成功",Toast.LENGTH_SHORT).show();


                                }else if(which==3){//详细名单
                                    SightModleTime sightModleTime1=sightModleTimeList.get(position);
                                    Intent intent=new Intent(ModleSelectActivity.this,SelectedTimeInfoActivity.class);
                                    Bundle bundle=new Bundle();//传递数据
                                    bundle.putInt("startHour",sightModleTime1.getStartHourTime());
                                    bundle.putInt("startMinute",sightModleTime1.getStartMinuteTime());
                                    bundle.putInt("endHour",sightModleTime1.getEndHourTime());
                                    bundle.putInt("endMinute",sightModleTime1.getEndMinuteTime());
                                    intent.putExtras(bundle);
                                    startActivity(intent);

                                }
                                dialog.dismiss();
                            }
                        });
                alertDialog=builder.create();
                alertDialog.show();
            }
        });
    }
}
