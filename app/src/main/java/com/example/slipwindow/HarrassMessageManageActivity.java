package com.example.slipwindow;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slipwindow.Adapter.AppAdapter;
import com.example.slipwindow.db.MessageHarrassRecorder;
import com.example.slipwindow.db.MessageWhiteListPass;
import com.example.slipwindow.entity.Contact;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

public class HarrassMessageManageActivity extends AppCompatActivity {
    private ListView recorderListView;
    private ArrayList<Contact> allAddressAndName;
    private ArrayList<String> allAddress;
    private AlertDialog alertDialog;
    private ArrayAdapter<String> arrayAdapter;
    private TextView state;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("骚扰短信");
        setContentView(R.layout.activity_harrass_message_manage);
        progressBar=(ProgressBar)findViewById(R.id.harrass_message_progress_bar);
        recorderListView=(ListView)findViewById(R.id.harrass_message_all_address_list_view);
        state=(TextView)findViewById(R.id.harrass_message_state_text);
        new Thread(runable).start();
        recorderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(HarrassMessageManageActivity.this);
                builder.setTitle("操作");
                builder.setItems(R.array.message_list_item_handle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){//删除
                            allAddress.remove(position);
                            Contact contact1=allAddressAndName.get(position);
                            allAddressAndName.remove(position);
                            arrayAdapter.notifyDataSetChanged();
                            deleteSelectedAddress(contact1.getAddress(),contact1.getName());
                            setTextState(allAddress);
                            Toast.makeText(HarrassMessageManageActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                        }else if(which==1){//恢复到系统
                            allAddress.remove(position);
                            Contact contact1=allAddressAndName.get(position);
                            allAddressAndName.remove(position);
                            arrayAdapter.notifyDataSetChanged();
                            setTextState(allAddress);
                            recoverSelectedToSystem(HarrassMessageManageActivity.this,contact1);//恢复手机
                            deleteSelectedAddress(contact1.getAddress(),contact1.getName());//数据库删除
                        }else if(which==2)//详细记录
                        {
                            Contact contact1=allAddressAndName.get(position);
                            Bundle bundle=new Bundle();
                            bundle.putString("name",contact1.getName());
                            bundle.putString("address",contact1.getAddress());
                            Intent intent=new Intent(HarrassMessageManageActivity.this,SelectedHarrassAddressInfoActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        dialog.dismiss();//关闭提示框
                    }
                });
                alertDialog=builder.create();
                alertDialog.show();
            }
        });

    }


    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            allAddressAndName=getAllAddressAndName(getAllMessageHarrass());
            allAddress=getAllAddress();
            myHandler.obtainMessage().sendToTarget();

        }
    };
    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            setTextState(allAddress);
            progressBar.setVisibility(View.GONE);//不可见
            arrayAdapter=new ArrayAdapter<String>(HarrassMessageManageActivity.this,android.R.layout.simple_list_item_1,allAddress);
            recorderListView.setAdapter(arrayAdapter);
        }

    };

    /**
     * 加载Menu布局
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.harrass_manage,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.delete_all_harrass://清空所有
                deleteAllAddress();
                allAddress.clear();
                allAddressAndName.clear();
                arrayAdapter.notifyDataSetChanged();
                setTextState(allAddress);
                Toast.makeText(HarrassMessageManageActivity.this,"已清空",Toast.LENGTH_SHORT).show();
                break;
            case R.id.recover_all_system://恢复所有到系统
                recoverAllToSystem(HarrassMessageManageActivity.this);
                allAddress.clear();
                allAddressAndName.clear();
                deleteAllAddress();
                arrayAdapter.notifyDataSetChanged();
                setTextState(allAddress);
                Toast.makeText(HarrassMessageManageActivity.this,"已全部恢复",Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home://启用返回键
                finish();
                break;
        }
        return true;
    }


    /**
     * 所有拦截短信
     * @return
     */
    public List<MessageHarrassRecorder> getAllMessageHarrass(){
        List<MessageHarrassRecorder> harrassRecorderList= DataSupport.findAll(MessageHarrassRecorder.class);
        return harrassRecorderList;
    }



    /**
     * 获取所有地址,和姓名
     * @param list
     * @return
     */
    public ArrayList<Contact> getAllAddressAndName(List<MessageHarrassRecorder> list){
        ArrayList<Contact> addressList=new ArrayList<Contact>();//所有地址

        for(MessageHarrassRecorder messageHarrassRecorder:list){
            int i=0;
            for(;i<addressList.size();i++){
                if(addressList.get(i).getName().equals(messageHarrassRecorder.getName())&&addressList.get(i).getAddress().equals(messageHarrassRecorder.getAddress())){
                    break;
                }
            }
            if(i>=addressList.size()){
                Contact contact=new Contact();
                contact.setAddress(messageHarrassRecorder.getAddress());
                contact.setName(messageHarrassRecorder.getName());
                addressList.add(contact);
            }
        }
        return addressList;
    }


    /**
     * 所有地址或名字
     * @return
     */
    public ArrayList<String> getAllAddress(){
        ArrayList<Contact> contacts=getAllAddressAndName(getAllMessageHarrass());
        ArrayList<String> allAddress=new ArrayList<String>();
        for(Contact contact:contacts){
            if(contact.getName().equals("")){//名字为空存地址，不为空存名字
                allAddress.add(contact.getAddress());
            }else{
                allAddress.add(contact.getName());
            }
        }
        return allAddress;
    }

    /**
     * 删除选中地址或名字所有信息
     * @param address
     */
    public void deleteSelectedAddress(String address,String name){
        DataSupport.deleteAll(MessageHarrassRecorder.class,"address=? and name=?",address,name);

    }

    /**
     * 清空所有
     */
    public void deleteAllAddress(){
        DataSupport.deleteAll(MessageHarrassRecorder.class);
    }

    /**
     * 恢复所有到系统
     * @param context
     */
    public void recoverAllToSystem(Context context){
        List<MessageHarrassRecorder> recorderList=getAllMessageHarrass();
        ContentResolver contentResolver=context.getContentResolver();//内容访问器
        ContentValues contentValues=new ContentValues();
        for(MessageHarrassRecorder messageHarrassRecorder:recorderList){
            contentValues.put(Telephony.Sms.ADDRESS,messageHarrassRecorder.getAddress());//地址
            contentValues.put(Telephony.Sms.BODY,messageHarrassRecorder.getBody());//内容
            contentValues.put(Telephony.Sms.DATE,messageHarrassRecorder.getDate());//时间
            contentValues.put(Telephony.Sms.READ,0);//未读
            contentValues.put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_INBOX);//收到的短信
            contentResolver.insert(Uri.parse("content://sms"),contentValues);
            Toast.makeText(context,"恢复成功",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 恢复选中联系人所有拦截记录
     * @param context
     * @param contact
     */
    public void recoverSelectedToSystem(Context context,Contact contact){
        List<MessageHarrassRecorder> list=DataSupport.where("address=? and name=?",contact.getAddress(),contact.getName()).find(MessageHarrassRecorder.class);
        ContentResolver contentResolver=context.getContentResolver();//内容访问器
        ContentValues contentValues=new ContentValues();
        for(MessageHarrassRecorder messageHarrassRecorder:list){
            contentValues.put(Telephony.Sms.ADDRESS,messageHarrassRecorder.getAddress());//地址
            contentValues.put(Telephony.Sms.BODY,messageHarrassRecorder.getBody());//内容
            contentValues.put(Telephony.Sms.DATE,messageHarrassRecorder.getDate());//时间
            contentValues.put(Telephony.Sms.READ,0);//未读
            contentValues.put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_INBOX);//收到的短信
            contentResolver.insert(Uri.parse("content://sms"),contentValues);
            Toast.makeText(context,"恢复成功",Toast.LENGTH_SHORT).show();
        }
    }

    public void setTextState(ArrayList<String> allAddress){
        if(allAddress.size()==0){
            state.setVisibility(View.VISIBLE);//可见
        }else{
            state.setVisibility(View.GONE);
        }

    }


}
