package com.example.slipwindow;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.TrafficStats;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slipwindow.db.MessageDarkListHarass;
import com.example.slipwindow.db.MobileUsedRecorder;
import com.example.slipwindow.service.FlowManageService;
import com.example.slipwindow.service.InformService;
import com.example.slipwindow.util.Common;
import com.example.slipwindow.util.FlowStorageManage;
import com.example.slipwindow.util.TextFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class FlowManageActivity extends AppCompatActivity {
    private LineChartView lineChartView;
    private ListView listView;
    private List<PointValue> pointValues=new ArrayList<PointValue>();//点
    private List<AxisValue> axisValues=new ArrayList<AxisValue>();//X轴坐标
    private List<MobileUsedRecorder> mobileUsedRecorders;
    private ArrayList<String> flowOder=new ArrayList<String>();
    private TextView textView;
    private LayoutInflater inflater;
    private TextView flowUpdateTextView;
    private View view1;
    private SharedPreferences pre;
    private Button button;
    private AlertDialog.Builder builder;
    private Timer timer;//定时检查是否过了一天
    private float totalMonthMobile;
    private AlertDialog dialog;
    String[] xValues={"1","2","3","4","5","6","7","8","9","10"};
    int[] values={1,22,3,24,0,88,56,5,100,123};

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("流量管理");
        setContentView(R.layout.activity_flow_manage);
        //关闭服务
        Intent intent1=new Intent(FlowManageActivity.this, FlowManageService.class);
        stopService(intent1);
        lineChartView=(LineChartView)findViewById(R.id.flow_manage_line_chart);
        textView=(TextView)findViewById(R.id.flow_manage_last);
        button=(Button)findViewById(R.id.flow_manage_update);
        lineChartView.setZoomEnabled(true);//设置支持缩放
        lineChartView.setValueSelectionEnabled(true);//数据选中显示
        listView=(ListView)findViewById(R.id.flow_manage_list_view);
        flowOder.add("流量排行");
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(FlowManageActivity.this,android.R.layout.simple_list_item_1,flowOder);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent intent=new Intent(FlowManageActivity.this,FlowOrderActivity.class);
                    startActivity(intent);
                }
            }
        });

        inflater= LayoutInflater.from(this);
        view1=inflater.inflate(R.layout.flow_update,null);
        flowUpdateTextView=(TextView)view1.findViewById(R.id.flow_update_text);
        flowUpdateTextView.setText("MB");
        flowUpdateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flowUpdateTextView.getText().toString().equals("MB")){
                    flowUpdateTextView.setText("GB");
                }else if(flowUpdateTextView.getText().toString().equals("GB")){
                    flowUpdateTextView.setText("MB");
                }
            }
        });
        pre=getSharedPreferences("phoneModle",MODE_PRIVATE);



        //修改本月已用流量弹出框
        builder = new AlertDialog.Builder(FlowManageActivity.this);
        builder.setTitle("本月已用流量")
                .setView(view1)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) view1.findViewById(R.id.flow_update_editText);
                        if (editText.getText() != null && !editText.getText().toString().equals("") && Float.parseFloat(editText.getText().toString()) >= 0) {//不为空并且大于0
                            SharedPreferences.Editor editor = pre.edit();
                            String used = editText.getText().toString();
                            if (flowUpdateTextView.getText().toString().equals("MB")) {
                                editor.putFloat("usedToatalMonthMobile", Float.parseFloat(used));//已经使用的月流量
                            } else if (flowUpdateTextView.getText().toString().equals("GB")) {
                                editor.putFloat("usedToatalMonthMobile", Float.parseFloat(used) * 1024);//已经使用的月流量
                            }
                            editor.apply();
                            float monthFlowMobile=pre.getFloat("monthFlowMobile",-1);
                            float s = pre.getFloat("usedToatalMonthMobile", 0);
                            initTopShow(s, totalMonthMobile,monthFlowMobile);
                        } else {
                            Toast.makeText(FlowManageActivity.this, "校正失败，输入有误", Toast.LENGTH_SHORT).show();
                        }

                    }

                }).setNegativeButton("取消", null);
        dialog = builder.create();
        setDraw();
    }


    /**
     * 更新显示已用和剩余多少流量框
     */
    public void updateTop() {
        Boolean hasNumber = pre.getBoolean("hasNumber", false);
        if (hasNumber) {//设置了套餐
            totalMonthMobile = pre.getFloat("totalMonthMobile", -1);//月流量套餐，单位为MB
            // long usedThisMonth=getUsedMonthMobile();//本月已用
            float usedThisMonth = 0;//本月已用
            if (pre.getFloat("usedToatalMonthMobile", -1) < 0) {
                SharedPreferences.Editor editor = pre.edit();
                float u = TextFormat.formatToMbFromByte(getUsedMonthMobile());
                usedThisMonth = u;
                editor.putFloat("usedToatalMonthMobile", u);
                editor.apply();
            } else {
                usedThisMonth = pre.getFloat("usedToatalMonthMobile", 0);
            }
            float monthFlowMobile=pre.getFloat("monthFlowMobile",-1);
            initTopShow(usedThisMonth, totalMonthMobile,monthFlowMobile);
            button.setVisibility(View.VISIBLE);
            button.setText("校准");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                }
            });
        }else{
            textView.setText("还未设置套餐");
            button.setVisibility(View.GONE);
        }
    }



    private void setDraw(){
        getAxisXLables();//获取所有计算日期流量
        updateTop();
        getAxisPoints();
        initLineChartView();
    }

    /**
     * 设置已用和剩余流量
     * @param usedThisMonth
     * @param totalMonthMobile
     */
    public void initTopShow(float usedThisMonth,float totalMonthMobile,float monthFlowMobile){
        if(totalMonthMobile>-1){//设置了套餐
            if(monthFlowMobile<0){
                if(usedThisMonth<=totalMonthMobile){
                    textView.setText("");
                    float last=totalMonthMobile-usedThisMonth;
                    textView.setText("已用："+usedThisMonth+"MB "+"剩余："+last+"MB");
                }else{//用超啦
                    textView.setText("");
                    float last=usedThisMonth-totalMonthMobile;
                    textView.setText("已用："+usedThisMonth+"MB "+"超额："+last+"MB");
                }
            }else{
                if(usedThisMonth<=monthFlowMobile){
                    textView.setText("");
                    float last=monthFlowMobile-usedThisMonth;
                    textView.setText("已用："+usedThisMonth+"MB "+"剩余："+last+"MB");

                }else{//用超啦
                    textView.setText("");
                    float last=usedThisMonth-monthFlowMobile;
                    textView.setText("已用："+usedThisMonth+"MB "+"超额："+last+"MB");
                }

            }
        }
    }


    /**
     * 设置X轴显示
     */
    private void getAxisXLables(){
       mobileUsedRecorders= FlowStorageManage.getAllMobileFrom(FlowManageActivity.this);
        SimpleDateFormat df = new SimpleDateFormat("dd");//设置日期格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        for(int i=0;i<mobileUsedRecorders.size();i++){
            try{
                String day=df.format(simpleDateFormat.parse(mobileUsedRecorders.get(mobileUsedRecorders.size()-1-i).getDate()));
                axisValues.add(new AxisValue(i).setLabel(day));
            }catch (ParseException e){

            }

        }
    }

    /**
     * 折线图每个点的显示
     */
    private void getAxisPoints(){
        SimpleDateFormat df = new SimpleDateFormat("dd");//设置日期格式
        for(int i=0;i<mobileUsedRecorders.size();i++){
            long mobileUsed=mobileUsedRecorders.get(mobileUsedRecorders.size()-1-i).getMobileUsedFlow();
            float mobileUsedMB=TextFormat.formatToMbFromByte(mobileUsed);
           // pointValues.add(new PointValue(i,mobileUsed));
            pointValues.add(new PointValue(i,mobileUsedMB));
           // new PointValue("1","12m");
        }
       /* for(int i=0;i<values.length;i++){
            pointValues.add(new PointValue(i,values[i]));
        }*/
    }

    private void initLineChartView(){
        Line line=new Line(pointValues).setColor(Color.parseColor("#FFCD41"));//Color.parseColor("#FFCD41")折线图颜色为绿色
        List<Line> lines=new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//数据点为圆形
        line.setCubic(false);//曲线折线
        line.setFilled(false);//不填充曲线面积
        //line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setHasLabelsOnlyForSelected(true);////点击数据坐标提示数据
        line.setHasLines(true);////是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line);
        LineChartData lineChartData=new LineChartData();
        lineChartData.setLines(lines);
        Axis axisX = new Axis(); //X轴
       // Axis axisY = new Axis();  //Y轴
        axisX.setValues(axisValues);  //填充X轴的坐标名称
        axisX.setMaxLabelChars(8);//缩放时最多8个
        axisX.setHasTiltedLabels(false);//x轴字体直立而非斜体
        axisX.setTextColor(Color.GREEN);
        lineChartData.setAxisXBottom(axisX);//x轴在底部
        axisX.setHasLines(true); //x 轴分割线
      // lineChartData.setAxisYLeft(axisY);
        lineChartView.setLineChartData(lineChartData);
        lineChartView.setVisibility(View.VISIBLE);
    }

    /**
     * 获取本月已用流量
     * @return
     */
    public long getUsedMonthMobile(){
        long used=0;
        if(mobileUsedRecorders.size()>0){
            for(MobileUsedRecorder mobileUsedRecorder:mobileUsedRecorders){
                used+=mobileUsedRecorder.getMobileUsedFlow();
            }
        }

        return used;
    }



    /**
     * 加载Menu布局\设置
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_flow_manage,menu);
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
            Intent intent=new Intent(FlowManageActivity.this,FlowManageSettingActivity.class);
           // startActivity(intent);
            startActivityForResult(intent,1);
        }
        return super.onOptionsItemSelected(item);
    }



    protected void onResume(){
        super.onResume();
        updateTop();

    }

    /**
     * 活动销毁时调用启动服务
     */
    protected void onDestroy(){
        super.onDestroy();
        Intent intent1=new Intent(FlowManageActivity.this, FlowManageService.class);
        startService(intent1);
    }


}
