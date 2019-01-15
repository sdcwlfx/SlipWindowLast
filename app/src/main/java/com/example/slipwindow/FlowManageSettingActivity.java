package com.example.slipwindow;
/**
 * 流量管理设置
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class FlowManageSettingActivity extends AppCompatActivity {
    private ArrayList<String> arrayList=new ArrayList<String>();
    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    private SharedPreferences pre;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("设置");
        setContentView(R.layout.activity_flow_manage_setting);
        listView=(ListView)findViewById(R.id.flow_mamage_setting_list_view);
        builder=new AlertDialog.Builder(FlowManageSettingActivity.this).setTitle("提示")
                .setMessage("请先设置套餐")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog=builder.create();
        arrayList.add("套餐设置");
        arrayList.add("校正流量数据");
        arrayList.add("限额提醒");
        arrayAdapter=new ArrayAdapter<String>(FlowManageSettingActivity.this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){//套餐设置
                    Intent intent=new Intent(FlowManageSettingActivity.this,FlowManagePackageActivity.class);
                    startActivity(intent);
                }else if(position==1){//校正流量数据
                    pre=getSharedPreferences("phoneModle",MODE_PRIVATE);
                    if(pre.getBoolean("hasNumber",false)){//设置了套餐
                        Intent intent=new Intent(FlowManageSettingActivity.this,FlowCorrectedActivity.class);
                        startActivity(intent);
                    }else{
                        alertDialog.show();
                    }

                }else if(position==2){//限额提醒
                    pre=getSharedPreferences("phoneModle",MODE_PRIVATE);
                    if(pre.getBoolean("hasNumber",false)){//设置了套餐
                        Intent intent=new Intent(FlowManageSettingActivity.this,FlowlimmitWarningActivity.class);
                        startActivity(intent);
                    }else{
                        alertDialog.show();
                    }
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
}
