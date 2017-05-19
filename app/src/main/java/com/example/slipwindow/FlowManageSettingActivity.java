package com.example.slipwindow;
/**
 * 流量管理设置
 */

import android.support.v7.app.ActionBar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("设置");
        setContentView(R.layout.activity_flow_manage_setting);
        listView=(ListView)findViewById(R.id.flow_mamage_setting_list_view);
        arrayList.add("套餐设置");
        arrayList.add("校正流量数据");
        arrayList.add("限额提醒");
        arrayAdapter=new ArrayAdapter<String>(FlowManageSettingActivity.this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){//套餐设置

                }else if(position==1){//校正流量数据

                }else if(position==2){//限额提醒

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
