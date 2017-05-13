package com.example.slipwindow.Adapter;
/**
 * 任务适配器
 */

import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.slipwindow.R;
import com.example.slipwindow.db.SightModleTime;
import com.example.slipwindow.entity.TaskInfo;
import com.example.slipwindow.util.TextFormat;

import java.util.List;

/**
 * Created by asus on 2017-04-26.
 */

public class TaskAdapter extends BaseAdapter{
    private List<TaskInfo> taskInfos;//所有时间段对象
    private LayoutInflater inflater;

    public TaskAdapter(Context context, List<TaskInfo> taskInfos){
        this.taskInfos=taskInfos;
        inflater=LayoutInflater.from(context);

    }
    public int getCount(){
        return taskInfos.size();
    }

    public TaskInfo getItem(int position){
        return taskInfos.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    /**
     * 对控件实例缓存
     */
    class ViewHolder{
        ImageView taskIcon;//任务图标
        TextView taskName;//任务名字
        TextView taskMemory;//任务内存
    }


    /**
     * 在每个子项被滚动到屏幕内被调用convertView将之前加载的布局缓存，position列表中显示出表项
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent){
        TaskInfo taskInfo=taskInfos.get(position);//获取当前App对象
        TaskAdapter.ViewHolder holder=null;
        if(convertView==null)//布局缓存为空，加载布局
        {
            convertView=inflater.inflate(R.layout.task_list_item,null);
            holder=new TaskAdapter.ViewHolder();//缓存控件实例项
            holder.taskIcon=(ImageView)convertView.findViewById(R.id.task_icon); //图标
            holder.taskName=(TextView) convertView.findViewById(R.id.task_name);//名字
            holder.taskMemory=(TextView)convertView.findViewById(R.id.task_memory);//内存
            convertView.setTag(holder);//全部加入布局缓存中
        }else{
            holder=(TaskAdapter.ViewHolder)convertView.getTag();
        }
        holder.taskIcon.setImageDrawable(taskInfo.getTaskIcon());//图标
        holder.taskName.setText(taskInfo.getTaskName());//名字
        holder.taskMemory.setText("占用内存："+ TextFormat.formatByte(taskInfo.getTaskMemeroy()*1024));//内存
        return convertView;
    }
}
