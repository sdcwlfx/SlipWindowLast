package com.example.slipwindow;
/**
 * App设置
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.slipwindow.service.FloatWindowService;

public class SettingActivity extends AppCompatActivity {
    private Switch susWindowSwitch;//悬浮窗
    private boolean susSelected;//悬浮选择

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("设置");
        setContentView(R.layout.activity_setting);
        Button aboutButton=(Button)findViewById(R.id.about_app_button);
        aboutButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent=new Intent(SettingActivity.this,AboutAppActivity.class);
                startActivity(intent);
            }
        });

        final SharedPreferences pre=getSharedPreferences("phoneModle",MODE_PRIVATE);
        susSelected=pre.getBoolean("susSelected",false);
        susWindowSwitch=(Switch)findViewById(R.id.float_switch);
        susWindowSwitch.setChecked(susSelected);
        susWindowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){//选择悬浮窗
                    SharedPreferences.Editor editor=pre.edit();
                    editor.putBoolean("susSelected",true);
                    editor.apply();
                    Intent intent=new Intent(SettingActivity.this, FloatWindowService.class);
                    startService(intent);
                }else{//关闭悬浮窗
                    Intent intent=new Intent(SettingActivity.this,FloatWindowService.class);
                    stopService(intent);

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
