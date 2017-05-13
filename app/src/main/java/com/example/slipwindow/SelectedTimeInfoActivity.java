package com.example.slipwindow;
/**
 * 选中时间段详细名单
 */

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slipwindow.Adapter.TimePeopleAdapter;
import com.example.slipwindow.db.MessageWhiteListPass;
import com.example.slipwindow.db.SightModleTime;
import com.example.slipwindow.db.TimePhone;
import com.example.slipwindow.util.Common;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class SelectedTimeInfoActivity extends AppCompatActivity {

    private ListView listView;;
    private FloatingActionButton floatingActionButton;//悬浮按钮
    private TextView textView;;
    private EditText editText;
    private String phoneNum;
    private AlertDialog dialog;
    private AlertDialog alertDialog;
    private List<MessageWhiteListPass> listPasses;
    private Bundle bundle;
    private SightModleTime sightModleTime;
    private ArrayList<TimePhone> timePeoples;//名单
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("详细名单");
        setContentView(R.layout.activity_selected_time_info);
        listView=(ListView)findViewById(R.id.selected_time_list);
        textView=(TextView)findViewById(R.id.selected_time_state_view);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.selected_time_button);//添加按钮
        Intent intent=getIntent();
        bundle=intent.getExtras();
        if(Common.getSelectedTime(bundle)==null){
            Toast.makeText(SelectedTimeInfoActivity.this,"没有该时间段",Toast.LENGTH_SHORT).show();
        }else {
            new Thread(runable).start();//开启线程
            //名单列表点击事件
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//从黑名单删除
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SelectedTimeInfoActivity.this);
                    builder.setTitle("操作");
                    builder.setItems(R.array.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                               // selectedTimePhones.remove(position);//删除
                                TimePhone timePhone1=timePeoples.get(position);
                                timePhone1.delete();//删除表项
                                timePeoples.remove(position);
                                //sightModleTime.setPhoneHarassList(selectedTimePhones);///////////////////////////////
                                sightModleTime.setTimePhones(timePeoples);
                                sightModleTime.save();//更新
                                new Thread(runable).start();//重新加载适配器
                                Toast.makeText(SelectedTimeInfoActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();//关闭提示框
                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();
                }
            });
            //悬浮按钮点击事件
            floatingActionButton.setOnClickListener(new View.OnClickListener() {//添加新号码到黑名单
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SelectedTimeInfoActivity.this);
                    builder.setTitle("选项");
                    builder.setItems(R.array.add_phone_to_dark_button_choice, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {//手动添加号码
                                editText = new EditText(SelectedTimeInfoActivity.this);
                                editText.setHint("输入11位手机号码");//提示信息
                                editText.setMaxLines(1);//最大行数
                                new AlertDialog.Builder(SelectedTimeInfoActivity.this).setTitle("请输入号码")
                                        .setView(editText)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                phoneNum = editText.getText().toString();
                                                if (phoneNum != null && Common.isMobile(phoneNum) && !phoneInTimeList(phoneNum)) {//保存到库表中
                                                    TimePhone timePhone1=new TimePhone();
                                                    timePhone1.setPhoneNum(phoneNum);
                                                    timePhone1.save();//保存到TimePhone表
                                                    sightModleTime.getTimePhones().add(timePhone1);
                                                    sightModleTime.save();////////
                                                    new Thread(runable).start();
                                                    Toast.makeText(SelectedTimeInfoActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                                                } else if (phoneInTimeList(phoneNum) && phoneNum != null && Common.isMobile(phoneNum)) {//已在黑名单中
                                                    Toast.makeText(SelectedTimeInfoActivity.this, "号码已在名单中", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    Toast.makeText(SelectedTimeInfoActivity.this, "请输入正确号码", Toast.LENGTH_SHORT).show();
                                                }


                                            }

                                        }).setNegativeButton("取消", null).show();


                            } else if (which == 1) {//从联系人添加
                                Intent intent = new Intent(SelectedTimeInfoActivity.this, SelectedTimeContactActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            }
                            dialog.dismiss();//关窗

                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                }

            });
        }
    }





    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            sightModleTime=Common.getSelectedTime(bundle);
            if(sightModleTime.getTimePhones().size()==0){
                timePeoples=new ArrayList<TimePhone>();
            }else{
                timePeoples=sightModleTime.getTimePhones();
            }
            myHandler.obtainMessage().sendToTarget();
        }
    };

    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            if(timePeoples.size()==0){
                textView.setVisibility(View.VISIBLE);//可见
                TimePeopleAdapter timePeopleAdapter=new TimePeopleAdapter(SelectedTimeInfoActivity.this,timePeoples);
                listView.setAdapter(timePeopleAdapter);
            }else{
                TimePeopleAdapter timePeopleAdapter=new TimePeopleAdapter(SelectedTimeInfoActivity.this,timePeoples);
                listView.setAdapter(timePeopleAdapter);

            }

        }

    };

    /**
     * 是否已在名单中
     * @param phoneNum
     * @return
     */
    public boolean phoneInTimeList(String phoneNum){
        boolean in=false;
        SightModleTime list=Common.getSelectedTime(bundle);
        if(list.getTimePhones()!=null){
            ArrayList<TimePhone> stringArrayList=list.getTimePhones();
            for(TimePhone phone:stringArrayList){
                if(phoneNum.equals(phone.getPhoneNum())){
                    in=true;
                    return in;
                }
            }
        }
        return in;

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
