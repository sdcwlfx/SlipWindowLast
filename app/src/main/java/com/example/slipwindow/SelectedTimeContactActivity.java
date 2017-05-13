package com.example.slipwindow;
/**
 * 未在该时段拦截名单中的联系人
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.slipwindow.Adapter.ContactsAdapter;
import com.example.slipwindow.db.PhoneWhiteListPass;
import com.example.slipwindow.db.SightModleTime;
import com.example.slipwindow.db.TimePhone;
import com.example.slipwindow.entity.Contact;
import com.example.slipwindow.util.Common;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class SelectedTimeContactActivity extends AppCompatActivity {

    private ListView contactListView;
    private ProgressBar progressBar;
    private ArrayList<Contact> allContactArrayList;//所有联系人
    private SightModleTime  sightModleTime;//时间段对象
    private ArrayList<Contact> lastContacts;
    private Bundle bundle;
    private ArrayList<TimePhone> timePhones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("联系人");
        setContentView(R.layout.activity_contacts);
        Intent intent=getIntent();
        bundle=intent.getExtras();
        progressBar=(ProgressBar)findViewById(R.id.contacts_progress_bar);//进度条
        contactListView=(ListView)findViewById(R.id.contact_list);
        if(Common.getSelectedTime(bundle)==null){
            Toast.makeText(SelectedTimeContactActivity.this,"获取时间段对象失败",Toast.LENGTH_SHORT).show();
        }else {
            new Thread(runable).start();
            contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//监听
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    final Contact contact1 = lastContacts.get(position);//获取选中联系人、//提示框添加，和取消
                    new AlertDialog.Builder(SelectedTimeContactActivity.this).setTitle("提示")
                            .setMessage("添加到名单")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String address = contact1.getAddress();
                                    Toast.makeText(SelectedTimeContactActivity.this, address, Toast.LENGTH_SHORT).show();
                                    TimePhone timePhone=new TimePhone();
                                    timePhone.setPhoneNum(address);
                                    timePhone.save();
                                    timePhones.add(timePhone);
                                    sightModleTime.getTimePhones().add(timePhone);
                                    sightModleTime.save();
                                     Toast.makeText(SelectedTimeContactActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                                    new Thread(runable).start();
                                }
                            }).setNegativeButton("否", null).show();
                }
            });
        }
    }

    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            allContactArrayList= Common.loadAllContactsFromPhone(SelectedTimeContactActivity.this);//所有联系人
            sightModleTime= Common.getSelectedTime(bundle);//获取时间段对象
            if(sightModleTime.getTimePhones()==null){
                timePhones=new ArrayList<TimePhone>();
            }else{
                timePhones=sightModleTime.getTimePhones();
            }
            lastContacts=getNoSelectedTimeContacts(allContactArrayList,timePhones);//未拉进名单的联系人
            myHandler.obtainMessage().sendToTarget();

        }
    };
    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            ContactsAdapter contactsAdapter=new ContactsAdapter(SelectedTimeContactActivity.this,lastContacts);
            progressBar.setVisibility(View.GONE);//进度条不可见
            contactListView.setAdapter(contactsAdapter);//与listView控件绑定

        }

    };

    /**
     * 获取未拉入该时段的联系人列表
     * @param allContacts
     * @param list
     * @return
     */

    public ArrayList<Contact> getNoSelectedTimeContacts(ArrayList<Contact> allContacts,ArrayList<TimePhone> list){
        if(list.size()>0){
            for(int i=0;i<allContacts.size();i++) {
                for (TimePhone phone : list) {
                    if (allContacts.get(i).getAddress().equals(phone.getPhoneNum())) {
                        allContacts.remove(i);
                        i--;
                        break;
                    }
                }
            }
        }

        return allContacts;

    }


    /**
     * 标题栏返回按钮
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==android.R.id.home)
        {
            Intent intent=new Intent(SelectedTimeContactActivity.this,SelectedTimeInfoActivity.class);
            intent.putExtras(bundle);
            finish();//结束当前活动
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 启用back键
     */
    public void onBackPressed(){

    }

}
