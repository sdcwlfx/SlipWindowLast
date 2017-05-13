package com.example.slipwindow;
/**
 * 未加入短信白名单的联系人
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
import com.example.slipwindow.db.MessageWhiteListPass;
import com.example.slipwindow.db.PhoneWhiteListPass;
import com.example.slipwindow.entity.Contact;
import com.example.slipwindow.util.Common;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MessageWhiteContactActivity extends AppCompatActivity {

    private ListView contactListView;
    private ProgressBar progressBar;
    private ArrayList<Contact> allContactArrayList;//所有联系人
    private List<MessageWhiteListPass> messageWhiteListPasses;//电话白名单
    private ArrayList<Contact> lastContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("联系人 ");
        setContentView(R.layout.activity_contacts);
        progressBar=(ProgressBar)findViewById(R.id.contacts_progress_bar);//进度条
        contactListView=(ListView)findViewById(R.id.contact_list);
        new Thread(runable).start();
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){//监听
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
                final Contact contact1=lastContacts.get(position);//获取选中联系人、//提示框添加，和取消
                new AlertDialog.Builder(MessageWhiteContactActivity.this).setTitle("提示")
                        .setMessage("添加到白名单")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MessageWhiteListPass messageWhiteListPass1=new MessageWhiteListPass();
                                messageWhiteListPass1.setPhone(contact1.getAddress());
                                messageWhiteListPass1.save();//添加到白名单
                                Toast.makeText(MessageWhiteContactActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                                new Thread(runable).start();
                            }
                        }).setNegativeButton("否",null).show();

            }
        });
    }

    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            allContactArrayList= Common.loadAllContactsFromPhone(MessageWhiteContactActivity.this);//所有联系人
            messageWhiteListPasses= DataSupport.findAll(MessageWhiteListPass.class);//电话白名单
            lastContacts=getNoSelectedContacts(allContactArrayList,messageWhiteListPasses);//未拉近黑名单的联系人
            myHandler.obtainMessage().sendToTarget();

        }
    };
    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            ContactsAdapter contactsAdapter=new ContactsAdapter(MessageWhiteContactActivity.this,lastContacts);
            progressBar.setVisibility(View.GONE);//进度条不可见
            contactListView.setAdapter(contactsAdapter);//与listView控件绑定

        }

    };

    /**
     * 获取未拉入白名单联系人列表
     * @param allContacts
     * @param messageWhiteListPasses
     * @return
     */

    public ArrayList<Contact> getNoSelectedContacts(ArrayList<Contact> allContacts,List<MessageWhiteListPass> messageWhiteListPasses){
        if(messageWhiteListPasses.size()>0){
            for(int i=0;i<allContacts.size();i++) {
                for (MessageWhiteListPass messageWhiteListPass : messageWhiteListPasses) {
                    if (allContacts.get(i).getAddress().equals(messageWhiteListPass.getPhone())) {
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
            Intent intent=new Intent(MessageWhiteContactActivity.this,MessageWhiteListActivity.class);
            finish();//结束当前活动
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){

    }
}
