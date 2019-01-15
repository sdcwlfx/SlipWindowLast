package com.example.slipwindow.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.slipwindow.R;
import com.example.slipwindow.entity.FlowCorrected;

import java.util.ArrayList;

/**
 * Created by asus on 2017-05-22.
 */

public class FlowCorrectedAdapter extends BaseAdapter {
    private ArrayList<FlowCorrected> flowCorrecteds ;//所有联系人对象
    private LayoutInflater inflater;

    public FlowCorrectedAdapter(Context context, ArrayList<FlowCorrected> flowCorrecteds){
        this.flowCorrecteds=flowCorrecteds;
        inflater=LayoutInflater.from(context);

    }
    public int getCount(){
        return flowCorrecteds.size();
    }

    public FlowCorrected getItem(int position){
        return flowCorrecteds.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    /**
     * 对控件实例缓存
     */
    class ViewHolder{
        TextView text1;
        TextView txet2;
        TextView text3;
    }

    /**
     * 在每个子项被滚动到屏幕内被调用convertView将之前加载的布局缓存，position列表中显示出表项
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    public View getView(int position, View convertView, ViewGroup parent){
        FlowCorrected flowCorrected=flowCorrecteds.get(position);//获取当前联系人对象
        FlowCorrectedAdapter.ViewHolder holder=null;
        if(convertView==null)//布局缓存为空，加载布局
        {
            convertView=inflater.inflate(R.layout.flow_corrected_list_item,null);//加载联系人子项
            holder=new FlowCorrectedAdapter.ViewHolder();//缓存控件实例项
            holder.text1=(TextView)convertView.findViewById(R.id.flow_corrected_list_item_text1);
            holder.txet2=(TextView)convertView.findViewById(R.id.flow_corrected_list_item_text2);
            holder.text3=(TextView)convertView.findViewById(R.id.flow_corrected_list_item_text3);
            convertView.setTag(holder);//全部加入布局缓存中
        }else{
            holder=(FlowCorrectedAdapter.ViewHolder)convertView.getTag();
        }
        holder.text1.setText(flowCorrected.getText1());
        holder.txet2.setText(flowCorrected.getText2());
        holder.text3.setText(flowCorrected.getText3());
        return convertView;
    }
}
