package com.example.slipwindow.Adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.slipwindow.R;
import com.example.slipwindow.entity.FlowAppUsedSituation;
import com.example.slipwindow.entity.TaskInfo;
import com.example.slipwindow.util.TextFormat;

import java.util.List;

/**
 * 流量排行列表适配器
 * Created by asus on 2017-05-26.
 */

public class FlowOrderAdapter extends BaseAdapter {
    private List<FlowAppUsedSituation> flowAppUsedSituations;//所有时间段对象
    private LayoutInflater inflater;
    private PackageManager packageManager;

    public FlowOrderAdapter(Context context, List<FlowAppUsedSituation> flowAppUsedSituations){
        this.flowAppUsedSituations=flowAppUsedSituations;
        packageManager=context.getPackageManager();
        inflater=LayoutInflater.from(context);

    }
    public int getCount(){
        return flowAppUsedSituations.size();
    }

    public FlowAppUsedSituation getItem(int position){
        return flowAppUsedSituations.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    /**
     * 对控件实例缓存
     */
    class ViewHolder{
        ImageView appIcon;//应用图标
        TextView appName;//应用名字
        TextView appUsedMobile;//应用使用流量
    }


    /**
     * 在每个子项被滚动到屏幕内被调用convertView将之前加载的布局缓存，position列表中显示出表项
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent){
        FlowAppUsedSituation flowAppUsedSituation=flowAppUsedSituations.get(position);//获取当前App对象
        FlowOrderAdapter.ViewHolder holder=null;
        if(convertView==null)//布局缓存为空，加载布局
        {
            convertView=inflater.inflate(R.layout.flow_order_list_item,null);
            holder=new FlowOrderAdapter.ViewHolder();//缓存控件实例项
            holder.appIcon=(ImageView)convertView.findViewById(R.id.flow_order_icon); //图标
            holder.appName=(TextView) convertView.findViewById(R.id.flow_order_app_name);//名字
            holder.appUsedMobile=(TextView)convertView.findViewById(R.id.flow_order_app_used_mobile);//内存
            convertView.setTag(holder);//全部加入布局缓存中
        }else{
            holder=(FlowOrderAdapter.ViewHolder)convertView.getTag();
        }
        holder.appIcon.setImageDrawable(flowAppUsedSituation.getApplicationInfo().loadIcon(packageManager));//图标
        holder.appName.setText(flowAppUsedSituation.getApplicationInfo().loadLabel(packageManager));//名字
        holder.appUsedMobile.setText("使用流量："+flowAppUsedSituation.getUsedMobile());//使用流量
        return convertView;
    }
}
