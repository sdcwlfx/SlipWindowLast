package com.example.slipwindow;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class ShowAppDetailInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("应用信息");
        setContentView(R.layout.activity_show_app_detail_info);
    }

    /**
     * 返回箭头
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
