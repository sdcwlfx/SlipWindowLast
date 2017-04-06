package com.example.slipwindow.Adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.slipwindow.R;
import com.example.slipwindow.entity.AppInfo;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by asus on 2017-04-06.
 */

public class AppAdapter extends BaseAdapter {
    private ArrayList<AppInfo> appList;//所有应用对象
    private LayoutInflater inflater;

    public AppAdapter(Context context, ArrayList<AppInfo> appList){
        this.appList=appList;
        inflater=LayoutInflater.from(context);

    }
    public int getCount(){
        return appList.size();
    }

    public AppInfo getItem(int position){
        return appList.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    /**
     * 对控件实例缓存
     */
    class ViewHolder{
        ImageView appImage;
        TextView appName;
    }

    /**
     * 在每个子项被滚动到屏幕内被调用convertView将之前加载的布局缓存，position列表中显示出表项
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    public View getView(int position, View convertView, ViewGroup parent){
        AppInfo appInfo=appList.get(position);//获取当前App对象
        ViewHolder holder=null;
        if(convertView==null)//布局缓存为空，加载布局
        {
            convertView=inflater.inflate(R.layout.app_list_item,null);
            holder=new ViewHolder();//缓存控件实例项
            holder.appImage=(ImageView)convertView.findViewById(R.id.app_image);
            holder.appName=(TextView)convertView.findViewById(R.id.app_name);
            convertView.setTag(holder);//全部加入布局缓存中
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.appImage.setImageDrawable(appInfo.getIcon());
        holder.appName.setText(appInfo.getAppNmae());
        return convertView;
    }


}
