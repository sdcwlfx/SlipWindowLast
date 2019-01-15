package com.example.slipwindow;
/**
 * 流量套餐设置
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slipwindow.Adapter.PackageSettingAdapter;
import com.example.slipwindow.entity.PackageSetting;
import com.example.slipwindow.service.FlowWarningListenService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class FlowManagePackageActivity extends AppCompatActivity {
    private ListView listView;
    private ListView listViewDay;
    private ArrayList<PackageSetting> arrayList=new ArrayList<PackageSetting>();
    private PackageSettingAdapter packageSettingAdapter;
    private SharedPreferences pre;
    private TextView flowUpdateTextView;
    private AlertDialog.Builder alertDialog;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private AlertDialog getDialog;
    private  View view1=null;
    private  View view2=null;
    private String unit="MB";//单位
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("套餐设置");
        setContentView(R.layout.activity_flow_manage_package);
        pre=getSharedPreferences("phoneModle",MODE_PRIVATE);
        listView=(ListView)findViewById(R.id.package_list_view);
       // PackageSetting packageSetting=new PackageSetting();
       // packageSetting.setText1("哈哈");
     //   packageSetting.setText2("dudu");
       // arrayList.add(packageSetting);


       // inflater=getLayoutInflater();
        inflater=LayoutInflater.from(this);
        view2=inflater.inflate(R.layout.flow_update_month_day,null);
        listViewDay=(ListView)view2.findViewById(R.id.flow_update_month_day_list);


        view1=inflater.inflate(R.layout.flow_update,null);
        flowUpdateTextView=(TextView)view1.findViewById(R.id.flow_update_text);
        flowUpdateTextView.setText("MB");
        flowUpdateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flowUpdateTextView.getText().toString().equals("MB")){
                    flowUpdateTextView.setText("GB");
                }else if(flowUpdateTextView.getText().toString().equals("GB")){
                    flowUpdateTextView.setText("MB");
                }
            }
        });



        init();//为列表获取数据到arrayList；适配列表

        listViewDay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor=getSharedPreferences("phoneModle",MODE_PRIVATE).edit();
                editor.putInt("day",position+1);
                editor.apply();
                dialog.dismiss();
                // init();//为列表获取数据到arrayList；适配列表
                arrayList.get(1).setText2(String.valueOf(position+1));
                packageSettingAdapter.notifyDataSetChanged();

            }
        });

        final AlertDialog.Builder builder=new AlertDialog.Builder(FlowManagePackageActivity.this);
        builder.setTitle("流量限额")
                .setView(view1)
                .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which){
                       EditText editText=(EditText)view1.findViewById(R.id.flow_update_editText);
                        unit=flowUpdateTextView.getText().toString();
                        float total=0;
                        if(editText.getText()!=null&&!editText.getText().toString().equals("")&&Float.parseFloat(editText.getText().toString())>0){//不为空并且大于0
                            String used=editText.getText().toString();
                            SharedPreferences.Editor editor=getSharedPreferences("phoneModle",MODE_PRIVATE).edit();
                            if(unit.equals("MB")){
                                total=Float.parseFloat(used);
                            }else if(unit.equals("GB")){
                                total=Float.parseFloat(used)*1024;
                            }
                            editor.putFloat("totalMonthMobile",total);//流量套餐
                            editor.putBoolean("hasNumber",true);
                            editor.apply();
                            arrayList.get(0).setText2(String.valueOf(total)+"MB");
                            packageSettingAdapter.notifyDataSetChanged();

                        }else{
                            Toast.makeText(FlowManagePackageActivity.this,"输入有误,设置失败",Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("取消",null);
             getDialog=builder.create();//必须先创建该对象再在列表点击事件中调用对象的.show方法才不会楚翔点击两次崩溃的情况




        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(FlowManagePackageActivity.this,android.R.layout.simple_list_item_1,getMonthDay());
        listViewDay.setAdapter(arrayAdapter);
        listViewDay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Calendar c=Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.MONTH,1);
                Date result=c.getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                String date=simpleDateFormat.format(result);
                SharedPreferences.Editor editor=getSharedPreferences("phoneModle",MODE_PRIVATE).edit();
                editor.putInt("day",position+1);
                editor.putString("nextMonth",date);//流量限额监听开启日期
                editor.apply();
                dialog.dismiss();
                // init();//为列表获取数据到arrayList；适配列表
                arrayList.get(1).setText2(String.valueOf(position+1));
                packageSettingAdapter.notifyDataSetChanged();
            }
        });
        alertDialog=new AlertDialog.Builder(FlowManagePackageActivity.this).setTitle("日期选择");
        alertDialog.setView(view2);
        dialog=alertDialog.create();//必须先创建该对象再在列表点击事件中调用对象的.show方法才不会楚翔点击两次崩溃的情况



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             //   Toast.makeText(FlowManagePackageActivity.this,String.valueOf(position),Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 0:// 设置套餐
                        getDialog.show();
                        break;
                    case 1://设置日期
                        dialog.show();
                        break;
                }
            }
        });
    }


    /**
     * 为列表获取数据到arrayList；适配列表
     */
    public void init(){
       // if(hasNumber){//设置了套餐

      //  Toast.makeText(FlowManagePackageActivity.this,arrayList.size(),Toast.LENGTH_SHORT).show();
        if(arrayList.size()>0){
            //arrayList.clear();
            Iterator<PackageSetting> iterator=arrayList.iterator();
            for(;iterator.hasNext();){
                iterator.next();
                iterator.remove();
            }
        }
        String totalMobile="";//月套餐
        String day="";//日期
        SharedPreferences pre1=getSharedPreferences("phoneModle",MODE_PRIVATE);
        float p=pre1.getFloat("totalMonthMobile",-1);
        if(p<0){//未设置套餐
            totalMobile="未设置";
        }else{
            totalMobile=String.valueOf(p)+"MB";
        }
        int d=pre1.getInt("day",-1);
        if(d<0){//未设置日期
            day="未设置";
        }else{
            day=String.valueOf(d);
        }
        PackageSetting packageSetting=new PackageSetting();
        packageSetting.setText1("套餐限额");
        packageSetting.setText2(totalMobile);
        arrayList.add(packageSetting);
        PackageSetting packageSetting1=new PackageSetting();
        packageSetting1.setText1("每月起始日期");
        packageSetting1.setText2(day);
        arrayList.add(packageSetting1);
        packageSettingAdapter=new PackageSettingAdapter(FlowManagePackageActivity.this,arrayList);
        listView.setAdapter(packageSettingAdapter);//为列表适配器


    }






    public ArrayList<String> getMonthDay(){
        ArrayList<String> month=new ArrayList<>();
        for(int i=1;i<=30;i++){
            String day="每月"+i+"号";
            month.add(day);
        }
        return month;
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
            float total=pre.getFloat("totalMonthMobile",-1);
            int day=pre.getInt("day",-1);
            if(total<0||day<0){
                new AlertDialog.Builder(FlowManagePackageActivity.this).setTitle("提示")
                        .setMessage("放弃设置套餐？")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog,int which){
                                SharedPreferences.Editor editor=pre.edit();
                                editor.putBoolean("hasNumber",false);
                                editor.putFloat("totalMonthMobile",-1);
                                editor.putInt("day",-1);
                                editor.apply();
                                finish();//结束当前活动
                            }
                        }).setNegativeButton("取消",null).show();

            }else{
                Intent intent=new Intent();
                setResult(RESULT_OK,intent);//返回值给上一个活动
                SharedPreferences.Editor editor=pre.edit();
                editor.putFloat("usedToatalMonthMobile",0);
                editor.putInt("monthWarningNumber",0);//月流量提示额
                editor.putInt("dayWarningMobile",0);//日流量提示额单位都是MB
                editor.apply();
                Intent intent1=new Intent(FlowManagePackageActivity.this, FlowWarningListenService.class);//开启服务监听流量限额
                startService(intent1);
                finish();
            }
            //finish();//结束当前活动
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * back键
     */
    public void onBackPressed(){
        float total=pre.getFloat("totalMonthMobile",-1);
        int day=pre.getInt("day",-1);
        if(total<0||day<0){
            new AlertDialog.Builder(FlowManagePackageActivity.this)
                    .setMessage("放弃设置套餐？")
                    .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog,int which){
                            SharedPreferences.Editor editor=pre.edit();
                            editor.putBoolean("hasNumber",false);
                            editor.putFloat("totalMonthMobile",-1);
                            editor.putInt("day",-1);
                            editor.apply();
                            finish();//结束当前活动
                        }
                    }).setNegativeButton("取消",null).show();

        }else{
            SharedPreferences.Editor editor=pre.edit();
            float total1=pre.getFloat("totalMonthMobile",0);
            editor.putFloat("usedToatalMonthMobile",0);//月已用流量
            editor.putInt("monthWarningNumber",(int)(total1*0.3));//月流量提示额
            editor.putInt("dayWarningMobile",(int)(total1*0.3));//日流量提示额单位都是MB
            editor.apply();
            Intent intent1=new Intent(FlowManagePackageActivity.this, FlowWarningListenService.class);//开启服务监听流量限额
            startService(intent1);
            Intent intent=new Intent();
            setResult(RESULT_OK,intent);//返回值给上一个活动
            finish();
        }
    }
}
