package com.example.slipwindow;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.slipwindow.service.IncomingCallAndMessageService;

import java.util.ArrayList;


public class ListManageActivity extends AppCompatActivity {

    private IncomingCallAndMessageService.PhoneNumberBinder phoneNumberBinder;
    private String phoneNumber;
    private TextView phoneText;
    private ListView phoneHarrassList;
    private ListView messageHarrassList;
    private Button listPhoneButton;
    private Button listMessageButton;
   // private String[] data={"白名单","黑名单"};
    private ArrayList<String> stringArrayList=new ArrayList<>();
    private ArrayList<String> messageArrayList=new ArrayList<>();

   //private ServiceConnection connection=new ServiceConnection() {

      // public void onServiceConnected(ComponentName name, IBinder service) {
          //  phoneNumberBinder=(IncomingCallAndMessageService.PhoneNumberBinder)service;
          //  phoneNumber=phoneNumberBinder.getPhoneNumber();//获取电话号码
           // phoneText.setText(phoneNumber);

      //  }

      //  @Override
      //  public void onServiceDisconnected(ComponentName name) {

     //   }
  //  };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("名单管理");
        setContentView(R.layout.activity_list_manage);
        stringArrayList.add("白名单");
        stringArrayList.add("黑名单");
        messageArrayList.add("白名单");
        messageArrayList.add("黑名单");
        messageArrayList.add("关键词黑名单");
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(ListManageActivity.this,android.R.layout.simple_list_item_1,stringArrayList);
        ArrayAdapter<String> adapter1=new ArrayAdapter<String>(ListManageActivity.this,android.R.layout.simple_list_item_1,messageArrayList);
        phoneHarrassList=(ListView)findViewById(R.id.phone_harass_listview);
        messageHarrassList=(ListView)findViewById(R.id.message_harass_listview);
        listMessageButton=(Button)findViewById(R.id.list_phone_button);
        listMessageButton.setOnClickListener(new View.OnClickListener(){//短信模式选择
            public void onClick(View view){
                Intent intent=new Intent(ListManageActivity.this,ModleMessageActivity.class);
                startActivity(intent);
            }
        });
        
        listPhoneButton=(Button)findViewById(R.id.list_manage_button);
        listPhoneButton.setOnClickListener(new View.OnClickListener(){//来电模式选择
            public void onClick(View view){
                Intent intent=new Intent(ListManageActivity.this,ModleSelectActivity.class);
                startActivity(intent);
            }
        });

        phoneHarrassList.setAdapter(adapter);//电话黑白名单
        messageHarrassList.setAdapter(adapter1);//短信黑白名单
        phoneHarrassList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view,int position,long id){
                if(position==0){
                    Intent intent=new Intent(ListManageActivity.this,PhoneWhiteListActivity.class);
                    startActivity(intent);//电话白名单
                }else if(position==1){
                    Intent intent=new Intent(ListManageActivity.this,PhoneDarkListActivity.class);
                    startActivity(intent);//电话黑名单
                }
            }
        });

        messageHarrassList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view,int position,long id){
                if(position==0){
                    Intent intent=new Intent(ListManageActivity.this,MessageWhiteListActivity.class);
                    startActivity(intent);//短信白名单
                }else if(position==1){
                    Intent intent=new Intent(ListManageActivity.this,MessageDarkListActivity.class);
                    startActivity(intent);//短信黑名单
                }else if(position==2){
                    Intent intent=new Intent(ListManageActivity.this,SensitiveWordActivity.class);
                    startActivity(intent);
                }


            }
        });

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
            finish();//结束当前活动
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
