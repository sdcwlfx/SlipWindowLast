package com.example.slipwindow;
/**
 * 指定联系人拦截短信
 */

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.slipwindow.Adapter.HarrassMessageAdapter;
import com.example.slipwindow.db.MessageHarrassRecorder;

import org.litepal.crud.DataSupport;

import java.util.List;

public class SelectedHarrassAddressInfoActivity extends AppCompatActivity {
    private List<MessageHarrassRecorder> recorderList;//拦截信息
    private RecyclerView recyclerView;
    private HarrassMessageAdapter harrassMessageAdapter;//适配器
    private Intent intent;
    private Bundle bundle;
    private ProgressBar progressBar;
    private  String address;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent=getIntent();
        bundle=intent.getExtras();
        address=bundle.getString("address");
        name=bundle.getString("name");
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(name.equals("")){
            actionBar.setTitle(address);
        }else{
            actionBar.setTitle(name);
        }
        setContentView(R.layout.activity_selected_harrass_address_info);
        progressBar=(ProgressBar)findViewById(R.id.selected_harrass_message_progress_bar);
        recyclerView=(RecyclerView)findViewById(R.id.selected_harrass_message_list_view);
        //recorderList=getSelectedAllMessage(name,address);
        new Thread(runable).start();

    }

    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            recorderList=getSelectedAllMessage(name,address);
            myHandler.obtainMessage().sendToTarget();

        }
    };
    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            progressBar.setVisibility(View.GONE);//不可见
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(SelectedHarrassAddressInfoActivity.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            harrassMessageAdapter=new HarrassMessageAdapter(recorderList,SelectedHarrassAddressInfoActivity.this);//适配
            recyclerView.setAdapter(harrassMessageAdapter);

        }

    };


    /**
     * 依据名字和地址获取所有拦截短信
     * @param name
     * @param address
     * @return
     */
    public List<MessageHarrassRecorder> getSelectedAllMessage(String name,String address){
        List<MessageHarrassRecorder> recorderList= DataSupport.where("name=? and address=?",name,address).find(MessageHarrassRecorder.class);
        return  recorderList;
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
