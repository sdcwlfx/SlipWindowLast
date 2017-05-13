package com.example.slipwindow;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slipwindow.Adapter.AppAdapter;
import com.example.slipwindow.Adapter.HarrassPhoneAdapter;
import com.example.slipwindow.db.MessageWhiteListPass;
import com.example.slipwindow.db.PhoneHarrassRecorder;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class HarrassPhoneManageActivity extends AppCompatActivity {
    private TextView state;
    private ListView phoneHarrassList;
    private ProgressBar progressBar;
    private List<PhoneHarrassRecorder> phoneHarrassRecorders;//电话拦截记录
    private AlertDialog alertDialog;
    private HarrassPhoneAdapter harrassPhoneAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("骚扰电话");
        setContentView(R.layout.activity_harrass_phone_manage);
        progressBar=(ProgressBar)findViewById(R.id.harrass_phone_progress_bar);
        state=(TextView)findViewById(R.id.harrass_phone_state_text);
        phoneHarrassList=(ListView)findViewById(R.id.harrass_phone_list);
        new Thread(runable).start();
        phoneHarrassList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(HarrassPhoneManageActivity.this);
                builder.setTitle("操作");
                builder.setItems(R.array.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            PhoneHarrassRecorder phoneHarrassRecorder=phoneHarrassRecorders.get(position);
                            phoneHarrassRecorder.delete();
                            phoneHarrassRecorders.remove(position);
                            setTextState(phoneHarrassRecorders);//为空时提示无拦截信息
                            harrassPhoneAdapter.notifyDataSetChanged();
                            Toast.makeText(HarrassPhoneManageActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();//关闭提示框
                    }
                });
                alertDialog=builder.create();
                alertDialog.show();
            }
        });


    }


    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            phoneHarrassRecorders=getAllPhoneHarrassRecord();//电话拦截记录
            myHandler.obtainMessage().sendToTarget();

        }
    };
    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            harrassPhoneAdapter=new HarrassPhoneAdapter(HarrassPhoneManageActivity.this,phoneHarrassRecorders);
            progressBar.setVisibility(View.GONE);
            setTextState(phoneHarrassRecorders);//为空时提示无拦截信息
            phoneHarrassList.setAdapter(harrassPhoneAdapter);

        }

    };


    /**
     * 加载Menu布局
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.phone_harrass_record,menu);
        return true;
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
        }else if(item.getItemId()==R.id.delete_all_phone_harrass){//右上角清空键
            phoneHarrassRecorders.clear();
            DataSupport.deleteAll(PhoneHarrassRecorder.class);//清空电话拦截记录
            setTextState(phoneHarrassRecorders);//为空时提示无拦截信息
            harrassPhoneAdapter.notifyDataSetChanged();
            Toast.makeText(HarrassPhoneManageActivity.this,"已清空",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 获取所有电话拦截记录
     * @return
     */
    public List<PhoneHarrassRecorder> getAllPhoneHarrassRecord(){
        List<PhoneHarrassRecorder> recorderList= DataSupport.findAll(PhoneHarrassRecorder.class);
        return recorderList;
    }

    /**
     * 为空时提示无拦截信息
     * @param recorderList
     */
    public void setTextState(List<PhoneHarrassRecorder> recorderList){
        if(recorderList.size()==0){
            state.setVisibility(View.VISIBLE);//可见
        }else{
            state.setVisibility(View.GONE);
        }

    }


}
