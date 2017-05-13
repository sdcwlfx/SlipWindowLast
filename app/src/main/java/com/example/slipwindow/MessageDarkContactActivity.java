package com.example.slipwindow;
/**
 * 未在信息黑名单的联系人
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
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
import com.example.slipwindow.db.MessageDarkListHarass;
import com.example.slipwindow.db.PhoneDarkListHarass;
import com.example.slipwindow.entity.Contact;
import com.example.slipwindow.util.Common;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MessageDarkContactActivity extends AppCompatActivity {
    private ListView contactListView;
    private ProgressBar progressBar;
    private ArrayList<Contact> allContactArrayList;//所有联系人
    private List<MessageDarkListHarass> messageDarkListHarasses;//电话黑名单
    private ArrayList<Contact> lastContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("联系人");
        setContentView(R.layout.activity_contacts);
        progressBar=(ProgressBar)findViewById(R.id.contacts_progress_bar);//进度条
        contactListView=(ListView)findViewById(R.id.contact_list);
        new Thread(runable).start();
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){//监听
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
                final Contact contact1=lastContacts.get(position);//获取选中联系人、//提示框添加，和取消
                new AlertDialog.Builder(MessageDarkContactActivity.this).setTitle("提示")
                        .setMessage("添加到黑名单")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MessageDarkListHarass messageDarkListHarass1=new MessageDarkListHarass();
                                messageDarkListHarass1.setPhoneNum(contact1.getAddress());
                                messageDarkListHarass1.save();//添加到黑名单
                                Toast.makeText(MessageDarkContactActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                                new Thread(runable).start();
                            }
                        }).setNegativeButton("否",null).show();

            }
        });
    }

    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            allContactArrayList= Common.loadAllContactsFromPhone(MessageDarkContactActivity.this);//所有联系人
            messageDarkListHarasses= DataSupport.findAll(MessageDarkListHarass.class);//电话黑名单
            lastContacts=getNoSelectedContacts(allContactArrayList,messageDarkListHarasses);//未拉近黑名单的联系人
            myHandler.obtainMessage().sendToTarget();

        }
    };
    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            ContactsAdapter contactsAdapter=new ContactsAdapter(MessageDarkContactActivity.this,lastContacts);
            progressBar.setVisibility(View.GONE);//进度条不可见
            contactListView.setAdapter(contactsAdapter);//与listView控件绑定

        }

    };

    /**
     * 获取未拉入黑名单联系人列表
     * @param allContacts
     * @param messageDarkListHarasses
     * @return
     */

    public ArrayList<Contact> getNoSelectedContacts(ArrayList<Contact> allContacts,List<MessageDarkListHarass> messageDarkListHarasses){
        if(messageDarkListHarasses.size()>0){
            for(int i=0;i<allContacts.size();i++) {
                for (MessageDarkListHarass messageDarkListHarass : messageDarkListHarasses) {
                    if (allContacts.get(i).getAddress().equals(messageDarkListHarass.getPhoneNum())) {
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
            Intent intent=new Intent(MessageDarkContactActivity.this,MessageDarkListActivity.class);
            finish();//结束当前活动
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 禁用Back键
     */
    public void onBackPressed(){

    }
}
