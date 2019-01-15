package com.example.slipwindow.Adapter;

/**
 * 指定联系人拦截短信
 */

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
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.slipwindow.HarrassMessageManageActivity;
import com.example.slipwindow.R;
import com.example.slipwindow.db.MessageDarkListHarass;
import com.example.slipwindow.db.MessageHarrassRecorder;
import com.example.slipwindow.db.SightModleTime;

import java.util.List;

/**
 * Created by asus on 2017-04-24.
 */

public class HarrassMessageAdapter extends RecyclerView.Adapter<HarrassMessageAdapter.ViewHodler>{
    private List<MessageHarrassRecorder> recorderList;
    private Context context;
    private AlertDialog alertDialog;

    static class ViewHodler extends RecyclerView.ViewHolder{
        View messageView;
       // LinearLayout leftLayout;
        TextView leftView;

        public ViewHodler(View view){
            super(view);
            messageView=view;//保存最外层布局
           // leftLayout=(LinearLayout)view.findViewById(R.id.left_layout);
            leftView=(TextView)view.findViewById(R.id.left_message);
        }
    }

    public HarrassMessageAdapter(List<MessageHarrassRecorder> recorderList, Context context){
        this.recorderList=recorderList;
        this.context=context;
    }

    public ViewHodler onCreateViewHolder(ViewGroup parent,int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_record_list_item,parent,false);
        final ViewHodler hodler=new ViewHodler(view);
        hodler.messageView.setOnClickListener(new View.OnClickListener(){//最外层布局点击事件
            public void onClick(View v) {
                final int position = hodler.getAdapterPosition();//获取点击位置
                final MessageHarrassRecorder messageHarrassRecorder = recorderList.get(position);
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("操作");
                builder.setItems(R.array.select_message_item_handle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){//删除
                            messageHarrassRecorder.delete();//删除
                            recorderList.remove(position);
                            notifyDataSetChanged();//刷新适配器
                        }else if(which==1)//回复到系统
                        {
                            ContentResolver contentResolver=context.getContentResolver();//内容访问器
                            ContentValues contentValues=new ContentValues();
                            contentValues.put(Telephony.Sms.ADDRESS,messageHarrassRecorder.getAddress());//地址
                            contentValues.put(Telephony.Sms.BODY,messageHarrassRecorder.getBody());//内容
                            contentValues.put(Telephony.Sms.DATE,messageHarrassRecorder.getDate());//时间
                            contentValues.put(Telephony.Sms.READ,0);//未读
                            contentValues.put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_INBOX);//收到的短信
                            contentResolver.insert(Uri.parse("content://sms"),contentValues);
                            messageHarrassRecorder.delete();//删除
                            recorderList.remove(position);
                            notifyDataSetChanged();//刷新适配器
                        }
                        dialog.dismiss();//关闭提示框
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        return hodler;
    }

    public void onBindViewHolder(ViewHodler hodler,int position){
        MessageHarrassRecorder messageHarrassRecorder=recorderList.get(position);
        hodler.leftView.setText(messageHarrassRecorder.getBody());//显示内容

    }
    public int getItemCount(){
        return recorderList.size();

    }

}
