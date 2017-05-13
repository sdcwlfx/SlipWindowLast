package com.example.slipwindow.Adapter;
/**
 * 电话拦截情景模式时间段适配器
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.slipwindow.R;
import com.example.slipwindow.db.SightModleTime;
import com.example.slipwindow.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2017-04-19.
 */

public class SightTimeAdapter extends BaseAdapter {
    private List<SightModleTime> sightModleTimes;//所有时间段对象
    private LayoutInflater inflater;

    public SightTimeAdapter(Context context, List<SightModleTime> sightModleTimes){
        this.sightModleTimes=sightModleTimes;
        inflater=LayoutInflater.from(context);

    }
    public int getCount(){
        return sightModleTimes.size();
    }

    public SightModleTime getItem(int position){
        return sightModleTimes.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    /**
     * 对控件实例缓存
     */
    class ViewHolder{
        TextView sightTime;//时间段
        TextView sightState;//是否启用
    }

    /**
     * 在每个子项被滚动到屏幕内被调用convertView将之前加载的布局缓存，position列表中显示出表项
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    public View getView(int position, View convertView, ViewGroup parent){
        SightModleTime sightModleTime=sightModleTimes.get(position);//获取当前App对象
        SightTimeAdapter.ViewHolder holder=null;
        if(convertView==null)//布局缓存为空，加载布局
        {
            convertView=inflater.inflate(R.layout.sight_time_list_item,null);
            holder=new SightTimeAdapter.ViewHolder();//缓存控件实例项
            holder.sightTime=(TextView) convertView.findViewById(R.id.sight_time);
            holder.sightState=(TextView)convertView.findViewById(R.id.sight_state);
            convertView.setTag(holder);//全部加入布局缓存中
        }else{
            holder=(SightTimeAdapter.ViewHolder)convertView.getTag();
        }
        holder.sightTime.setText(sightModleTime.getStartHourTime()+":"+sightModleTime.getStartMinuteTime()+"-"+sightModleTime.getEndHourTime()+":"+sightModleTime.getEndMinuteTime());//时间段
        holder.sightState.setText(sightModleTime.getState());//状态是启用还是禁用
        return convertView;
    }
}
