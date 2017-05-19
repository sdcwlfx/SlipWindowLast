package com.example.slipwindow;
/**
 * 流量套餐设置
 */

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.slipwindow.Adapter.PackageSettingAdapter;
import com.example.slipwindow.entity.PackageSetting;

import java.util.ArrayList;

public class FlowManagePackageActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<PackageSetting> arrayList=new ArrayList<PackageSetting>();
    private PackageSettingAdapter packageSettingAdapter;
    private SharedPreferences pre=getSharedPreferences("phoneModle",MODE_PRIVATE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("套餐设置");
        setContentView(R.layout.activity_flow_manage_package);
        listView=(ListView)findViewById(R.id.package_list_view);
        init();//为列表获取数据到arrayList；适配列表
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){//设置套餐
                    
                }else if(position==1){//设置日期

                }
            }
        });





    }


    /**
     * 为列表获取数据到arrayList；适配列表
     */
    public void init(){
        Boolean hasNumber=pre.getBoolean("hasNumber",false);//是否设置了套餐
       // if(hasNumber){//设置了套餐
        arrayList.clear();
        String totalMobile="";//月套餐
        String day="";//日期
        float p=pre.getFloat("totalMonthMobile",-1);
        if(p<0){//未设置套餐
            totalMobile="未设置";
        }else{
            totalMobile=String.valueOf(p)+"MB";
        }
        int d=pre.getInt("day",-1);
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
        packageSetting.setText2(day);
        arrayList.add(packageSetting1);
        packageSettingAdapter=new PackageSettingAdapter(FlowManagePackageActivity.this,arrayList);
        listView.setAdapter(packageSettingAdapter);//为列表适配器
    }
}
