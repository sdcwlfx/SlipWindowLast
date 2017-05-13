package com.example.slipwindow.Adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.provider.Telephony;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.slipwindow.R;
import com.example.slipwindow.db.MessageHarrassRecorder;
import com.example.slipwindow.entity.AppInfo;

import java.util.List;

/**
 * 上网控制适配器
 */

public class NetControlAdapter extends RecyclerView.Adapter<NetControlAdapter.ViewHodler> {
    private List<AppInfo> appInfos;
    private Context context;
    private AlertDialog alertDialog;

    static class ViewHodler extends RecyclerView.ViewHolder{
        ImageView appImage;
        RadioButton flowRadioButton;
        RadioButton wifiRadionButton;

        public ViewHodler(View view){
            super(view);
            appImage=(ImageView)view.findViewById(R.id.net_app_icon);
            flowRadioButton=(RadioButton)view.findViewById(R.id.net_flow_radio_button);
            wifiRadionButton=(RadioButton)view.findViewById(R.id.net_wifi_radio_button);
        }
    }

    public NetControlAdapter(List<AppInfo> appInfos, Context context){
        this.appInfos=appInfos;
        this.context=context;
    }

    public ViewHodler onCreateViewHolder(ViewGroup parent,int viewType){
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.net_control_list_item,parent,false);
        ViewHodler hodler=new ViewHodler(view);
        hodler.flowRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//数据设定
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


            }
        });
        hodler.wifiRadionButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//WIFI设定
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


            }
        });
        return hodler;
    }




    public void onBindViewHolder(NetControlAdapter.ViewHodler hodler, int position){
        AppInfo appInfo=appInfos.get(position);
        hodler.appImage.setImageDrawable(appInfo.getIcon());
        hodler.flowRadioButton.setChecked(true);
        hodler.wifiRadionButton.setChecked(true);
    }
    public int getItemCount(){
        return appInfos.size();

    }

}
