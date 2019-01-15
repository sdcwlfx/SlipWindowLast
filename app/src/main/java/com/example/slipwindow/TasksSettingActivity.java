package com.example.slipwindow;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.slipwindow.service.ScreenOffService;
import com.example.slipwindow.util.Common;

public class TasksSettingActivity extends AppCompatActivity {
    private Switch settingSwitch;
    private SharedPreferences pre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("设置");
        setContentView(R.layout.activity_tasks_setting);
        settingSwitch=(Switch)findViewById(R.id.task_setting_switch);
        pre=getSharedPreferences("phoneModle",MODE_PRIVATE);
        Boolean setting=pre.getBoolean("taskSetting",false);
        settingSwitch.setChecked(setting);
        settingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(Common.isRoot()){//获取Root权限
                        SharedPreferences.Editor editor=pre.edit();
                        editor.putBoolean("taskSetting",true);
                        editor.apply();
                        Intent intent=new Intent(TasksSettingActivity.this, ScreenOffService.class);
                        startService(intent);//开启锁屏监听服务
                    }else{
                        AlertDialog.Builder dialog1=new AlertDialog.Builder(TasksSettingActivity.this);
                        dialog1.setTitle("提示").setMessage("请先获取Root权限");
                        dialog1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                settingSwitch.setChecked(false);
                                //finish();
                            }
                        });
                        dialog1.show();
                    }

                }else{
                    if(Common.isRoot()){
                        SharedPreferences.Editor editor=pre.edit();
                        editor.putBoolean("taskSetting",false);
                        editor.apply();
                        Intent intent=new Intent(TasksSettingActivity.this, ScreenOffService.class);
                        stopService(intent);//关闭锁屏监听服务
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
