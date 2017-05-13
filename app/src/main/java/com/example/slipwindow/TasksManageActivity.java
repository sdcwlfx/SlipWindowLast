package com.example.slipwindow;
/**
 * 任务管理主页：当前运行任务列表
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.slipwindow.Adapter.TaskAdapter;
import com.example.slipwindow.db.MessageDarkListHarass;
import com.example.slipwindow.entity.TaskInfo;
import com.example.slipwindow.util.Common;
import com.example.slipwindow.util.TasksUtil;

import java.util.ArrayList;
import java.util.List;

public class TasksManageActivity extends AppCompatActivity {
    private Button userButton;//用户布局显示用户进程
    private Button systemButton;//用户布局显示系统进程
    private Button userButton1;//系统布局显示用户进程
    private Button systemButton1;//系统布局显示系统进程
    private ListView userTaskListView;
    private ListView systemTaskListView;
    private List<TaskInfo> userTasks=new ArrayList<TaskInfo>();//存储用户进程
    private List<TaskInfo> systemTasks=new ArrayList<TaskInfo>();//存储系统进程
    private TaskAdapter useTaskAdapter;
    private TaskAdapter systemAdapter;
    private AlertDialog alertDialog;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("任务管理");
        setContentView(R.layout.activity_user_tasks_manage);//加载用户应用布局
        new Thread(runable).start();

       // loadUserTasksLayout();///加载用户进程
    }

    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            TasksUtil.getTaskInfos(TasksManageActivity.this,userTasks,systemTasks);//获取用户进程、系统进程
            myHandler.obtainMessage().sendToTarget();
        }
    };
    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            loadUserTasksLayout();//加载用户应用布局
           /* if(userTasks.size()>0){
                useTaskAdapter=new TaskAdapter(TasksManageActivity.this,userTasks);
            }
            if(systemTasks.size()>0){
                systemAdapter=new TaskAdapter(TasksManageActivity.this,systemTasks);
            }
            if(userTasks.size()>0){
                userTaskListView.setAdapter(useTaskAdapter);
            }else{
                Toast.makeText(TasksManageActivity.this,"用户应用进程为空",Toast.LENGTH_SHORT).show();
            }*/
        }

    };

    /**
     * 加载Menu布局\设置
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
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
        }else if(item.getItemId()==R.id.action_settings){//设置
            Intent intent=new Intent(TasksManageActivity.this,TasksSettingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 加载应用进程
     */
    public void loadUserTasksLayout(){
        setContentView(R.layout.activity_user_tasks_manage);//加载用户应用布局
        progressBar=(ProgressBar)findViewById(R.id.user_tasks_progress_bar);
        progressBar.setVisibility(View.GONE);//隐藏加载框
        userButton=(Button)findViewById(R.id.user_layout_button);
        systemButton=(Button)findViewById(R.id.system_layout_button);
        userButton.setVisibility(View.VISIBLE);
        systemButton.setVisibility(View.VISIBLE);
        userTaskListView=(ListView)findViewById(R.id.user_tasks_listview);
       // if((userTasks.size()==0)||(systemTasks.size()==0)) {
        if(userTasks.size()>0){
            useTaskAdapter=new TaskAdapter(TasksManageActivity.this,userTasks);
        }
        if(systemTasks.size()>0){
            systemAdapter=new TaskAdapter(TasksManageActivity.this,systemTasks);
        }
        if(userTasks.size()>0){
            userTaskListView.setAdapter(useTaskAdapter);
        }else{
            Toast.makeText(TasksManageActivity.this,"用户应用进程为空",Toast.LENGTH_SHORT).show();
        }
          //  new Thread(runable).start();
       // }
        userTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(TasksManageActivity.this);
                builder.setTitle("操作");
                builder.setItems(R.array.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            if(Common.isRoot()){//已有root权限
                                TaskInfo taskInfo=userTasks.get(position);
                                // TasksUtil.CloseSelectedTask(taskInfo);
                                TasksUtil.CloseSelectedTask(TasksManageActivity.this,taskInfo);
                                userTasks.remove(position);
                                useTaskAdapter.notifyDataSetChanged();//同步适配器
                                //  Toast.makeText(TasksManageActivity.this,"已关闭",Toast.LENGTH_SHORT).show();
                            }else{
                                AlertDialog.Builder dialog1=new AlertDialog.Builder(TasksManageActivity.this);
                                dialog1.setTitle("提示").setMessage("请先获取Root权限");
                                dialog1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                       // finish();
                                    }
                                });
                                dialog1.show();
                            }
                        }
                        dialog.dismiss();//关闭提示框
                    }
                });
                alertDialog=builder.create();
                alertDialog.show();
            }
        });
        systemButton.setOnClickListener(new View.OnClickListener() {//加载系统应用布局
            @Override
            public void onClick(View v) {
                loadSystemTasksLayout();
            }
        });
    }


    /**
     * 加载系统应用布局
     */
    public void loadSystemTasksLayout(){
        setContentView(R.layout.activity_system_tasks_manage);//加载系统应用布局
        userButton1=(Button)findViewById(R.id.user_layout_button1);
        systemButton1=(Button)findViewById(R.id.system_layout_button1);
        systemTaskListView=(ListView) findViewById(R.id.system_tasks_listview);
        if(systemTasks.size()==0){
            Toast.makeText(TasksManageActivity.this,"系统进程为空",Toast.LENGTH_SHORT).show();
        }
        systemTaskListView.setAdapter(systemAdapter);
        userButton1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //setContentView(R.layout.activity_user_tasks_manage);//加载用户应用布局
                loadUserTasksLayout();
            }
        });
    }
}
