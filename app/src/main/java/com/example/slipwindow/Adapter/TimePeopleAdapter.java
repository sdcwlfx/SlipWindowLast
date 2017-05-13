package com.example.slipwindow.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.slipwindow.R;
import com.example.slipwindow.db.SightModleTime;
import com.example.slipwindow.db.TimePhone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2017-04-22.
 */

public class TimePeopleAdapter extends BaseAdapter {
    private ArrayList<TimePhone> timePhones;//所有时间段对象
    private LayoutInflater inflater;

    public TimePeopleAdapter(Context context, ArrayList<TimePhone> timePhones){
        this.timePhones=timePhones;
        inflater=LayoutInflater.from(context);

    }
    public int getCount(){
        return timePhones.size();
    }

    public TimePhone getItem(int position){
        return timePhones.get(position);
    }

    public long getItemId(int position){
        return position;
    }



    /**
     * 对控件实例缓存
     */
    class ViewHolder{
        TextView phone;//电话

    }

    /**
     * 在每个子项被滚动到屏幕内被调用convertView将之前加载的布局缓存，position列表中显示出表项
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    public View getView(int position, View convertView, ViewGroup parent){
        TimePhone timePhone=timePhones.get(position);//获取当前App对象
        TimePeopleAdapter.ViewHolder holder=null;
        if(convertView==null)//布局缓存为空，加载布局
        {
            convertView=inflater.inflate(R.layout.time_phone,null);
            holder=new TimePeopleAdapter.ViewHolder();//缓存控件实例项
            holder.phone=(TextView) convertView.findViewById(R.id.time_phone_number);
            convertView.setTag(holder);//全部加入布局缓存中
        }else{
            holder=(TimePeopleAdapter.ViewHolder)convertView.getTag();
        }
        holder.phone.setText(timePhone.getPhoneNum());//显示电话
        return convertView;
    }

}
