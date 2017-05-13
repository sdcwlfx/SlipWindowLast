package com.example.slipwindow.Adapter;
/**
 * 拦截电话适配器：联系人名字、时间
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.slipwindow.R;
import com.example.slipwindow.db.PhoneHarrassRecorder;

import java.util.List;

/**
 * Created by asus on 2017-04-24.
 */

public class HarrassPhoneAdapter extends BaseAdapter{
    private List<PhoneHarrassRecorder> phoneHarrassRecorders;//所有时间段对象
    private LayoutInflater inflater;

    public HarrassPhoneAdapter(Context context, List<PhoneHarrassRecorder> phoneHarrassRecorders){
        this.phoneHarrassRecorders=phoneHarrassRecorders;
        inflater=LayoutInflater.from(context);

    }
    public int getCount(){
        return phoneHarrassRecorders.size();
    }

    public PhoneHarrassRecorder getItem(int position){
        return phoneHarrassRecorders.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    /**
     * 对控件实例缓存
     */
    class ViewHolder{
        TextView harrassText;//时间段
        TextView harrassTime;//是否启用
    }

    /**
     * 在每个子项被滚动到屏幕内被调用convertView将之前加载的布局缓存，position列表中显示出表项
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    public View getView(int position, View convertView, ViewGroup parent){
        PhoneHarrassRecorder phoneHarrassRecorder=phoneHarrassRecorders.get(position);//获取当前App对象
        HarrassPhoneAdapter.ViewHolder holder=null;
        if(convertView==null)//布局缓存为空，加载布局
        {
            convertView=inflater.inflate(R.layout.harrass_phone_list_item,null);
            holder=new HarrassPhoneAdapter.ViewHolder();//缓存控件实例项
            holder.harrassText=(TextView) convertView.findViewById(R.id.phone_harrass_text);
            holder.harrassTime=(TextView)convertView.findViewById(R.id.phone_harrass_time);
            convertView.setTag(holder);//全部加入布局缓存中
        }else{
            holder=(HarrassPhoneAdapter.ViewHolder)convertView.getTag();
        }
        if(phoneHarrassRecorder.getName().equals("")){
            holder.harrassText.setText(phoneHarrassRecorder.getAddress());//号码
        }else{
            holder.harrassText.setText(phoneHarrassRecorder.getName());//联系人
        }
        holder.harrassTime.setText(phoneHarrassRecorder.getDate());//时间
        return convertView;
    }
}
