package com.example.slipwindow;
/**
 * 短信拦截模式选择
 */

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioGroup;

public class ModleMessageActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("模式选择");
        setContentView(R.layout.activity_modle_message);
        radioGroup=(RadioGroup)findViewById(R.id.radio_message_group);//默认选择标准模式
        int check=getCheckedRadioButton();//获取选中模式
        setCheckedRadioButton(check,radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.radio_message_dark){
                    updateModleSelected(2);
                }else if(checkedId==R.id.radio_mesage_white){
                    updateModleSelected(1);
                }
            }
        });
    }

    /**
     * 获取选中的模式1、2、3、4、5、6
     * @return
     */
    public int getCheckedRadioButton(){
        SharedPreferences preferences=getSharedPreferences("phoneModle",MODE_PRIVATE);
        int checked=preferences.getInt("selectMessage",0);
        return checked;
    }

    /**
     * 设置选中的拦截模式
     * @param checked
     * @param radioGroup
     */
    public void setCheckedRadioButton(int checked,RadioGroup radioGroup) {
        if (checked == 1) {
            radioGroup.check(R.id.radio_mesage_white);//仅过白名单
        } else if (checked == 2) {
            radioGroup.check(R.id.radio_message_dark);//仅拦黑名单
        }
    }

    /**
     * 更新选择的拦截模式
     * @param check
     */
    public void updateModleSelected(int check){
        SharedPreferences.Editor editor=getSharedPreferences("phoneModle",MODE_PRIVATE).edit();
        editor.putInt("selectMessage",check);
        editor.apply();
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
