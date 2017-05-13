package com.example.slipwindow.Adapter;

/**
 * 系统联系人适配器
 * Created by asus on 2017-04-17.
 */
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.slipwindow.R;
import com.example.slipwindow.entity.AppInfo;
import com.example.slipwindow.entity.Contact;

import java.util.ArrayList;
import java.util.Objects;

public class ContactsAdapter extends BaseAdapter {


    private ArrayList<Contact> contacts ;//所有联系人对象
    private LayoutInflater inflater;

    public ContactsAdapter(Context context, ArrayList<Contact> contacts){
        this.contacts=contacts;
        inflater=LayoutInflater.from(context);

    }
    public int getCount(){
        return contacts.size();
    }

    public Contact getItem(int position){
        return contacts.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    /**
     * 对控件实例缓存
     */
    class ViewHolder{
        TextView contactName;
        TextView contactAddress;
    }

    /**
     * 在每个子项被滚动到屏幕内被调用convertView将之前加载的布局缓存，position列表中显示出表项
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    public View getView(int position, View convertView, ViewGroup parent){
        Contact contact=contacts.get(position);//获取当前联系人对象
        ViewHolder holder=null;
        if(convertView==null)//布局缓存为空，加载布局
        {
            convertView=inflater.inflate(R.layout.contact_list_item,null);//加载联系人子项
            holder=new ViewHolder();//缓存控件实例项
            holder.contactName=(TextView)convertView.findViewById(R.id.contact_name);
            holder.contactAddress=(TextView)convertView.findViewById(R.id.contact_address);
            convertView.setTag(holder);//全部加入布局缓存中
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.contactName.setText(contact.getName());
        holder.contactAddress.setText(contact.getAddress());
        return convertView;
    }


}

