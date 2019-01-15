package com.example.slipwindow.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.slipwindow.R;
import com.example.slipwindow.entity.FlowWarningFirst;
import com.example.slipwindow.entity.PackageSetting;

import java.util.ArrayList;

/**
 * Created by asus on 2017-05-23.
 */

public class FlowWarningFirstAdapter extends BaseAdapter {

    private ArrayList<FlowWarningFirst> flowWarningFirsts ;//所有联系人对象
    private LayoutInflater inflater;

    public FlowWarningFirstAdapter(Context context, ArrayList<FlowWarningFirst> flowWarningFirsts){
        this.flowWarningFirsts=flowWarningFirsts;
        inflater=LayoutInflater.from(context);

    }
    public int getCount(){
        return flowWarningFirsts.size();
    }

    public FlowWarningFirst getItem(int position){
        return flowWarningFirsts.get(position);
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
    }

    /**
     * 在每个子项被滚动到屏幕内被调用convertView将之前加载的布局缓存，position列表中显示出表项
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    public View getView(int position, View convertView, ViewGroup parent){
        FlowWarningFirst flowWarningFirst=flowWarningFirsts.get(position);//获取当前联系人对象
        FlowWarningFirstAdapter.ViewHolder holder=null;
        if(convertView==null)//布局缓存为空，加载布局
        {
            convertView=inflater.inflate(R.layout.flow_warning_list1_item,null);//加载联系人子项
            holder=new FlowWarningFirstAdapter.ViewHolder();//缓存控件实例项
            holder.text1=(TextView)convertView.findViewById(R.id.flow_warning_list1_item_text1);
            holder.txet2=(TextView)convertView.findViewById(R.id.flow_warning_list1_item_text2);
            convertView.setTag(holder);//全部加入布局缓存中
        }else{
            holder=(FlowWarningFirstAdapter.ViewHolder)convertView.getTag();
        }
        holder.text1.setText(flowWarningFirst.getText1());
        holder.txet2.setText(flowWarningFirst.getText2());
        return convertView;
    }
}
