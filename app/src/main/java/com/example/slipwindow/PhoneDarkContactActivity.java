package com.example.slipwindow;
/**
 * 联系人列表，点击某联系人弹出是否添加到黑名单
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
import com.example.slipwindow.db.PhoneDarkListHarass;
import com.example.slipwindow.entity.Contact;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class PhoneDarkContactActivity extends AppCompatActivity {
    private ListView contactListView;
    private ProgressBar progressBar;
    private ArrayList<Contact> allContactArrayList;//所有联系人
    private List<PhoneDarkListHarass> phoneDarkListHarasses;//电话黑名单
    private ArrayList<Contact> lastContacts;
    private ArrayList<Contact> noSelectedContacts;

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
                // final Contact contact1=noSelectedContacts.get(position);
                new AlertDialog.Builder(PhoneDarkContactActivity.this).setTitle("提示")
                        .setMessage("添加到黑名单")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PhoneDarkListHarass phoneDarkListHarass1=new PhoneDarkListHarass();
                                phoneDarkListHarass1.setPhoneNum(contact1.getAddress());
                                phoneDarkListHarass1.save();//添加到黑名单
                                Toast.makeText(PhoneDarkContactActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                        //        lastContacts.remove(position);
                                new Thread(runable).start();
                                // noSelectedContacts.remove(position);
                        //        ContactsAdapter contactsAdapter1=new ContactsAdapter(PhoneDarkContactActivity.this,lastContacts);
                                //ContactsAdapter contactsAdapter1=new ContactsAdapter(PhoneDarkContactActivity.this,noSelectedContacts);
                         //       contactListView.setAdapter(contactsAdapter1);//重新绑定适配器
                            }
                        }).setNegativeButton("否",null).show();

            }
        });
    }

    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            allContactArrayList=loadAllContactsFromPhone();//所有联系人
            phoneDarkListHarasses= DataSupport.findAll(PhoneDarkListHarass.class);//电话黑名单
            Toast.makeText(PhoneDarkContactActivity.this,"黑名单人数："+phoneDarkListHarasses.size(),Toast.LENGTH_SHORT).show();
            lastContacts=getNoSelectedContacts(allContactArrayList,phoneDarkListHarasses);//未拉近黑名单的联系人
            //Toast.makeText(PhoneDarkContactActivity.this,"未被拉黑联系人",Toast.LENGTH_SHORT).show();
           // getNoSelectedContacts(allContactArrayList,phoneDarkListHarasses);//未拉近黑名单的联系人
            myHandler.obtainMessage().sendToTarget();

        }
    };
    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            ContactsAdapter contactsAdapter=new ContactsAdapter(PhoneDarkContactActivity.this,lastContacts);
            // ContactsAdapter contactsAdapter=new ContactsAdapter(PhoneDarkContactActivity.this,noSelectedContacts);
            progressBar.setVisibility(View.GONE);//进度条不可见
            contactListView.setAdapter(contactsAdapter);//与listView控件绑定

        }

    };


    /**
     * 加载所有联系人
     */
    public  ArrayList<Contact> loadAllContactsFromPhone(){
        Cursor cursor=null;
        ArrayList<Contact> contacts=new ArrayList<Contact>();
       // noSelectedContacts=new ArrayList<Contact>();

        try{
            cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
            if(cursor!=null){
                while (cursor.moveToNext()){
                    String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));//获取联系人姓名
                    String address=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));//获取联系人手机号
                    if(address.contains("-"))//规定电话格式
                        address=address.replace("-","");
                    if(address.contains(" "))
                        address=address.replace(" ","");
                    Contact contact=new Contact();
                    contact.setName(name);
                    contact.setAddress(address);
                    contacts.add(contact);
                   // noSelectedContacts.add(contact);
                }
            }
        }catch (Exception e){

        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
       // Toast.makeText(PhoneDarkContactActivity.this,"获得所有联系人",Toast.LENGTH_SHORT).show();
        return contacts;
    }


    /**
     * 获取未拉入黑名单联系人列表
     * @param allContacts
     * @param phoneDarkListHarasses
     * @return
     */

  public ArrayList<Contact> getNoSelectedContacts(ArrayList<Contact> allContacts,List<PhoneDarkListHarass> phoneDarkListHarasses){
      //  public void getNoSelectedContacts(ArrayList<Contact> allContacts,List<PhoneDarkListHarass> phoneDarkListHarasses){
       /* for(Contact contact:allContacts){
            for(PhoneDarkListHarass phoneDarkListHarass:phoneDarkListHarasses){
                if(contact.getAddress().equals(phoneDarkListHarass.getPhoneNum())){
                   // noSelectedContacts.remove(contact);//若已在黑名单列表中，移除对象
                    break;
                }
            }
        }*/
    /*  if(phoneDarkListHarasses.size()>0){
          Iterator<Contact> iterator=allContacts.iterator();//迭代器
          while (iterator.hasNext()){
              for(PhoneDarkListHarass phoneDarkListHarass:phoneDarkListHarasses){
                  if(iterator.next().getAddress().equals(phoneDarkListHarass.getPhoneNum())){
                      iterator.remove();
                      break;
                  }
              }
          }
          Toast.makeText(PhoneDarkContactActivity.this,"剩余联系人数："+allContacts.size(),Toast.LENGTH_SHORT).show();
          return allContacts;
      }*/
      if(phoneDarkListHarasses.size()>0){
          for(int i=0;i<allContacts.size();i++) {
              for (PhoneDarkListHarass phoneDarkListHarass : phoneDarkListHarasses) {
                  if (allContacts.get(i).getAddress().equals(phoneDarkListHarass.getPhoneNum())) {
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
            Intent intent=new Intent(PhoneDarkContactActivity.this,PhoneDarkListActivity.class);
            finish();//结束当前活动
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){

    }
}
