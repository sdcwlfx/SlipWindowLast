package com.example.slipwindow.Adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.slipwindow.R;
import com.example.slipwindow.entity.FlowWarningSecond;
import com.example.slipwindow.util.TextFormat;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by asus on 2017-05-23.
 */

public class FlowWarningSecondAdapter extends RecyclerView.Adapter<FlowWarningSecondAdapter.ViewHodler>{

    private List<FlowWarningSecond> flowWarningSeconds;
    private int position1;
    private Context context;
    private DecimalFormat decimalFormat;
    private SharedPreferences.Editor editor;

    static class ViewHodler extends RecyclerView.ViewHolder{
        TextView textView1;
        TextView textView2;
        SeekBar seekBar;
        TextView TextView3;

        public ViewHodler(View view){
            super(view);
            textView1=(TextView)view.findViewById(R.id.flow_warning_list2_item_text1);
            textView2=(TextView)view.findViewById(R.id.flow_warning_list2_item_text2);
            seekBar=(SeekBar)view.findViewById(R.id.flow_warning_list2_item_seek_bar);
            TextView3=(TextView)view.findViewById(R.id.flow_warning_list2_item_text3);
        }
    }

    public FlowWarningSecondAdapter(List<FlowWarningSecond> flowWarningSeconds, Context context){
        this.flowWarningSeconds=flowWarningSeconds;
        this.context=context;
        decimalFormat=new DecimalFormat(".00");
        editor=context.getSharedPreferences("phoneModle",Context.MODE_PRIVATE).edit();
    }

    public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_warning_list2_item,parent,false);
        final ViewHodler hodler=new ViewHodler(view);
        hodler.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //进度条拖动过程中调用 显示用了百分比String.valueOf(Float.parseFloat(decimalFormat.format(flowWarningSecond.getSeekBarNum()/(float)flowWarningSecond.getMaxMobile()))*100)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                position1=hodler.getAdapterPosition();//点击的行数
                FlowWarningSecond flowWarningSecond=flowWarningSeconds.get(position1);//获取对象
                if(progress!=0){
                    String percent=String.valueOf((int)(Float.parseFloat(decimalFormat.format(progress/(float)flowWarningSecond.getMaxMobile()))*100))+"%";//百分比
                    String p= TextFormat.formatFromMb(progress);//当前值
                    flowWarningSeconds.get(position1).setText2("到限定值的"+percent+"("+p+")"+"提醒");
                    flowWarningSeconds.get(position1).setSeekBarNum(progress);
                    flowWarningSeconds.get(position1).setPercent(percent);
                }
               // notifyDataSetChanged();
               // notifyItemChanged(position1);

            }
            //开始拖动调用
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            //停止拖动调用
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(position1==0){//更新月提醒额
                    editor.putInt("monthWarningNumber",flowWarningSeconds.get(position1).getSeekBarNum());
                    editor.apply();
                }else if(position1==1){//更新日提醒额
                    editor.putInt("dayWarningNumber",flowWarningSeconds.get(position1).getSeekBarNum());
                    editor.apply();
                }
                notifyItemChanged(position1);
            }
        });
        return hodler;
    }

    public void onBindViewHolder(FlowWarningSecondAdapter.ViewHodler hodler, int position){
        FlowWarningSecond flowWarningSecond=flowWarningSeconds.get(position);
        hodler.textView1.setText(flowWarningSecond.getText1());
        hodler.textView2.setText(flowWarningSecond.getText2());//单位MB
        hodler.seekBar.setMax(flowWarningSecond.getMaxMobile());//最大值,单位MB
        hodler.seekBar.setProgress(flowWarningSecond.getSeekBarNum());//进度值
        hodler.TextView3.setText(flowWarningSecond.getPercent());//百分比
       // hodler.leftView.setText(messageHarrassRecorder.getBody());//显示内容

    }
    public int getItemCount(){
        return flowWarningSeconds.size();

    }



}
