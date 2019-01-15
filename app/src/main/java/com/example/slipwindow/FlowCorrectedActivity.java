package com.example.slipwindow;
/**
 * 流量矫正
 */

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slipwindow.Adapter.FlowCorrectedAdapter;
import com.example.slipwindow.Adapter.PackageSettingAdapter;
import com.example.slipwindow.entity.FlowCorrected;
import com.example.slipwindow.entity.PackageSetting;

import java.util.ArrayList;
import java.util.Iterator;

public class FlowCorrectedActivity extends AppCompatActivity {
    private LayoutInflater inflater;
    private View view=null;
    private View view1=null;
    private TextView flowUpdateUnitTextView;
    private TextView flowCorrectedUnitTextView;
    private ListView listView;
    private FlowCorrectedAdapter flowCorrectedAdapter;
    private ArrayList<FlowCorrected> flowCorrecteds=new ArrayList<FlowCorrected>();
    private String unit="MB";//单位
    private AlertDialog getDialog;
    private AlertDialog getDialog1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("校正流量数据");
        setContentView(R.layout.activity_flow_corrected);
        listView=(ListView)findViewById(R.id.flow_corrected_list_view);
        inflater= LayoutInflater.from(this);
        view=inflater.inflate(R.layout.flow_update,null);
        flowUpdateUnitTextView=(TextView)view.findViewById(R.id.flow_update_text);
        flowUpdateUnitTextView.setText("MB");
        flowUpdateUnitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flowUpdateUnitTextView.getText().toString().equals("MB")){
                    flowUpdateUnitTextView.setText("GB");
                }else if(flowUpdateUnitTextView.getText().toString().equals("GB")){
                    flowUpdateUnitTextView.setText("MB");
                }
            }
        });

        view1=inflater.inflate(R.layout.flow_corrected__use_month,null);
        flowCorrectedUnitTextView=(TextView)view1.findViewById(R.id.flow_corrected_text);
        flowCorrectedUnitTextView.setText("MB");
        flowCorrectedUnitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flowCorrectedUnitTextView.getText().toString().equals("MB")){
                    flowCorrectedUnitTextView.setText("GB");
                }else if(flowCorrectedUnitTextView.getText().toString().equals("GB")){
                    flowCorrectedUnitTextView.setText("MB");
                }
            }
        });

        unit();


        final AlertDialog.Builder builder=new AlertDialog.Builder(FlowCorrectedActivity.this);
        builder.setTitle("月度流量总额")
                .setView(view)
                .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which){
                        EditText editText=(EditText)view.findViewById(R.id.flow_update_editText);
                        unit=flowUpdateUnitTextView.getText().toString();
                        float total=0;
                        if(editText.getText()!=null&&!editText.getText().toString().equals("")&&Float.parseFloat(editText.getText().toString())>0){//不为空并且大于0
                            String used=editText.getText().toString();
                            SharedPreferences.Editor editor=getSharedPreferences("phoneModle",MODE_PRIVATE).edit();
                            if(unit.equals("MB")){
                                total=Float.parseFloat(used);
                            }else if(unit.equals("GB")){
                                total=Float.parseFloat(used)*1024;
                            }
                            editor.putFloat("monthFlowMobile",total);//月度总流量，包含上月流量
                            editor.putBoolean("hasNumber",true);
                            editor.apply();
                            flowCorrecteds.get(0).setText3(String.valueOf(total)+"MB");
                            flowCorrectedAdapter.notifyDataSetChanged();

                        }else{
                            Toast.makeText(FlowCorrectedActivity.this,"输入有误,设置失败",Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("取消",null);
        getDialog=builder.create();//必须先创建该对象再在列表点击事件中调用对象的.show方法才不会楚翔点击两次崩溃的情况



        final AlertDialog.Builder builder1=new AlertDialog.Builder(FlowCorrectedActivity.this);
        builder1.setTitle("本月已用流量")
                .setView(view1)
                .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which){
                        EditText editText=(EditText)view1.findViewById(R.id.flow_corrected_editText);
                        unit=flowCorrectedUnitTextView.getText().toString();
                        float total=0;
                        if(editText.getText()!=null&&!editText.getText().toString().equals("")&&Float.parseFloat(editText.getText().toString())>0){//不为空并且大于0
                            String used=editText.getText().toString();
                            SharedPreferences.Editor editor=getSharedPreferences("phoneModle",MODE_PRIVATE).edit();
                            if(unit.equals("MB")){
                                total=Float.parseFloat(used);
                            }else if(unit.equals("GB")){
                                total=Float.parseFloat(used)*1024;
                            }
                            editor.putFloat("usedToatalMonthMobile",total);//月度总流量，包含上月流量
                            editor.putBoolean("hasNumber",true);
                            editor.apply();
                            flowCorrecteds.get(1).setText3(String.valueOf(total)+"MB");
                            flowCorrectedAdapter.notifyDataSetChanged();

                        }else{
                            Toast.makeText(FlowCorrectedActivity.this,"输入有误,设置失败",Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("取消",null);
        getDialog1=builder1.create();//必须先创建该对象再在列表点击事件中调用对象的.show方法才不会楚翔点击两次崩溃的情况


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //   Toast.makeText(FlowManagePackageActivity.this,String.valueOf(position),Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 0:// 设置套餐
                        getDialog.show();
                        break;
                    case 1://设置日期
                        getDialog1.show();
                        break;
                }
            }
        });
    }



    public void unit(){
        if(flowCorrecteds.size()>0){
            //arrayList.clear();
            Iterator<FlowCorrected> iterator=flowCorrecteds.iterator();
            for(;iterator.hasNext();){
                iterator.next();
                iterator.remove();
            }
        }
        String totalMobile="";//月流量，包含上月剩的
        String usedMonth="";//
        SharedPreferences pre1=getSharedPreferences("phoneModle",MODE_PRIVATE);
        float p=pre1.getFloat("totalMonthMobile",-1);
        float q=pre1.getFloat("monthFlowMobile",-1);//月流量，包含上月流量
        if(q<0&&p<0){//未设置套餐
            totalMobile="未设置";
        }else{
            if(q<0){
                totalMobile=String.valueOf(p)+"MB";
            }else{
                totalMobile=String.valueOf(q)+"MB";
            }
        }
        float d=pre1.getFloat("usedToatalMonthMobile",-1);
        if(d<0){//未设置日期
            usedMonth="未设置";
        }else{
            usedMonth=String.valueOf(d)+"MB";
        }
        FlowCorrected flowCorrected=new FlowCorrected();
        flowCorrected.setText1("月度总流量");
        flowCorrected.setText2("本月流量+上月剩余流量，建议手动校准。");
        flowCorrected.setText3(totalMobile);
        flowCorrecteds.add(flowCorrected);
        FlowCorrected flowCorrected1=new FlowCorrected();
        flowCorrected1.setText1("本月已用");
        flowCorrected1.setText3(usedMonth);
        flowCorrecteds.add(flowCorrected1);
        flowCorrectedAdapter=new FlowCorrectedAdapter(FlowCorrectedActivity.this,flowCorrecteds);
        listView.setAdapter(flowCorrectedAdapter);//为列表适配器

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
