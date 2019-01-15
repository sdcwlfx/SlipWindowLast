package com.example.slipwindow;
/**
 * 限额提醒
 */

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.slipwindow.Adapter.FlowWarningFirstAdapter;
import com.example.slipwindow.Adapter.FlowWarningSecondAdapter;
import com.example.slipwindow.entity.FlowWarningFirst;
import com.example.slipwindow.entity.FlowWarningSecond;
import com.example.slipwindow.util.TextFormat;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FlowlimmitWarningActivity extends AppCompatActivity {
    private ListView listView;
    private RecyclerView recyclerView;
    private ArrayList<FlowWarningFirst> flowWarningFirsts=new ArrayList<FlowWarningFirst>();
    private FlowWarningFirstAdapter flowWarningFirstAdapter;
    private FlowWarningSecondAdapter flowWarningSecondAdapter;
    private ArrayList<FlowWarningSecond> flowWarningSeconds=new ArrayList<FlowWarningSecond>();
    private SharedPreferences pre;
    private SharedPreferences.Editor editor;
    private String monthMore;//月度流量超限时，方案：断网(默认)和提醒
    private float monthTotalMobile;//总流量
    private int monthWarningNumber;//月提醒额monthWarningNumber
    private int dayWarningNumber;//日提醒额dayWarningNumber
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private DecimalFormat decimalFormat=new DecimalFormat(".00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("限额提醒");
        setContentView(R.layout.activity_flowlimmit_warning);
        listView=(ListView)findViewById(R.id.flow_warning_list_view1);
        recyclerView=(RecyclerView)findViewById(R.id.flow_warning_list_view2);
        pre=getSharedPreferences("phoneModle",MODE_PRIVATE);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        builder=new AlertDialog.Builder(FlowlimmitWarningActivity.this);
        builder.setItems(R.array.flow_warning, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){//0为提醒，1为断网
                    case 0:
                        flowWarningFirsts.get(0).setText2("");
                        flowWarningFirsts.get(0).setText2("提醒");
                        flowWarningFirstAdapter.notifyDataSetChanged();
                        editor.putString("monthMore","提醒");
                        editor.apply();
                        flowWarningFirstAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        flowWarningFirsts.get(0).setText2("");
                        flowWarningFirsts.get(0).setText2("断网");
                        flowWarningFirstAdapter.notifyDataSetChanged();
                        editor.putString("monthMore","断网");
                        editor.apply();
                        flowWarningFirstAdapter.notifyDataSetChanged();
                        Toast.makeText(FlowlimmitWarningActivity.this,"5.0以上系统不支持断网",Toast.LENGTH_SHORT).show();
                        break;
                }
                dialog.dismiss();

            }
        });
        alertDialog=builder.create();
        unit();

    }


    public void unit(){
        monthMore=pre.getString("monthMore","断网");//月度流量超限时
        int month=pre.getInt("monthWarningNumber",-1);
        int day=pre.getInt("dayWarningNumber",-1);

        if(pre.getFloat("monthFlowMobile",-1)<0){
            monthTotalMobile=pre.getFloat("totalMonthMobile",-1);
        }else{
            monthTotalMobile=pre.getFloat("monthFlowMobile",-1);
        }
        if(month<=0){
            monthWarningNumber=(int)(monthTotalMobile*0.3);
        }else{
            monthWarningNumber=month;
        }
        if(day<0){
            dayWarningNumber=(int)(monthTotalMobile*0.3);
        }else{
            dayWarningNumber=day;
        }
        String monthPercent=String.valueOf((int)(Float.parseFloat(decimalFormat.format(monthWarningNumber/monthTotalMobile))*100))+"%";//百分比
        String dayPercent=String.valueOf((int)(Float.parseFloat(decimalFormat.format(dayWarningNumber/monthTotalMobile))*100))+"%";//百分比
        editor=pre.edit();
        FlowWarningFirst flowWarningFirst=new FlowWarningFirst();
        flowWarningFirst.setText1("月度流量超限时");
        flowWarningFirst.setText2(monthMore);
        flowWarningFirsts.add(flowWarningFirst);
        flowWarningFirstAdapter=new FlowWarningFirstAdapter(FlowlimmitWarningActivity.this,flowWarningFirsts);
        listView.setAdapter(flowWarningFirstAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alertDialog.show();
            }
        });

        FlowWarningSecond flowWarningSecond=new FlowWarningSecond();
        flowWarningSecond.setPercent(monthPercent);
        flowWarningSecond.setText1("月已用流量提醒");
        flowWarningSecond.setText2("到限定值得"+monthPercent+"("+TextFormat.formatFromMb(monthWarningNumber)+")"+"提醒");
        flowWarningSecond.setSeekBarNum(monthWarningNumber);
        flowWarningSecond.setMaxMobile((int)monthTotalMobile);
        flowWarningSeconds.add(flowWarningSecond);
        FlowWarningSecond flowWarningSecond1=new FlowWarningSecond();
        flowWarningSecond1.setPercent(monthPercent);
        flowWarningSecond1.setText1("日已用流量提醒");
        flowWarningSecond1.setText2("到限定值得"+dayPercent+"("+TextFormat.formatFromMb(dayWarningNumber)+")"+"提醒");
        flowWarningSecond1.setSeekBarNum(dayWarningNumber);
        flowWarningSecond1.setMaxMobile((int)monthTotalMobile);
        flowWarningSeconds.add(flowWarningSecond1);
        flowWarningSecondAdapter=new FlowWarningSecondAdapter(flowWarningSeconds,FlowlimmitWarningActivity.this);
        recyclerView.setAdapter(flowWarningSecondAdapter);
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
