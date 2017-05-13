package com.example.slipwindow;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class HarrassManageActivity extends AppCompatActivity {
    private Button phoneRecord;
    private Button messageRecord;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("骚扰拦截");
        setContentView(R.layout.activity_harrass_manage);
       /* phoneRecord=(Button)findViewById(R.id.phone_harrass_recorder);
        phoneRecord.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent=new Intent(HarrassManageActivity.this,HarrassPhoneManageActivity.class);
                startActivity(intent);
            }
        });
        messageRecord=(Button)findViewById(R.id.message_harrass_recorder);
        messageRecord.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent=new Intent(HarrassManageActivity.this,HarrassMessageManageActivity.class);
                startActivity(intent);
            }
        });*/
        listView=(ListView)findViewById(R.id.harrass_manage_list);
        ArrayList<String> arrayList=new ArrayList<String>();
        arrayList.add("短信拦截记录");
        arrayList.add("来电拦截记录");
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(HarrassManageActivity.this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent intent=new Intent(HarrassManageActivity.this,HarrassMessageManageActivity.class);
                    startActivity(intent);
                }else if(position==1){
                    Intent intent=new Intent(HarrassManageActivity.this,HarrassPhoneManageActivity.class);
                    startActivity(intent);
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
