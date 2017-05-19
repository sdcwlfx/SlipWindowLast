package com.example.slipwindow;
/**
 * 短信黑名单列表
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slipwindow.db.MessageDarkListHarass;
import com.example.slipwindow.db.PhoneDarkListHarass;
import com.example.slipwindow.util.Common;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MessageDarkListActivity extends AppCompatActivity {

    private ListView messageDarkList;
    private FloatingActionButton floatingActionButton;//悬浮按钮
    private TextView messageDarkStateView;
    private ArrayList<String> messageDarkArrayList;
    private EditText editText;
    private String phoneNum;
    private AlertDialog dialog;
    private AlertDialog alertDialog;
    private List<MessageDarkListHarass> listHarasses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("短信黑名单");
        setContentView(R.layout.activity_meaage_dark_list);
        messageDarkList=(ListView)findViewById(R.id.message_dark_list);
        messageDarkStateView=(TextView)findViewById(R.id.message_dark_list_state_view);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.add_new_message_to_dark_list_button);//添加按钮
        new Thread(runable).start();//开启线程
        //黑名单列表点击事件
        messageDarkList.setOnItemClickListener(new AdapterView.OnItemClickListener(){//从黑名单删除
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
                AlertDialog.Builder builder=new AlertDialog.Builder(MessageDarkListActivity.this);
                builder.setTitle("操作");
                builder.setItems(R.array.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            MessageDarkListHarass harass=listHarasses.get(position);
                            harass.delete();
                            new Thread(runable).start();//重新加载适配器
                            Toast.makeText(MessageDarkListActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();//关闭提示框
                    }
                });
                alertDialog=builder.create();
                alertDialog.show();
            }
        });
        //悬浮按钮点击事件
        floatingActionButton.setOnClickListener(new View.OnClickListener(){//添加新号码到黑名单
            public void onClick(View v){
                AlertDialog.Builder builder=new AlertDialog.Builder(MessageDarkListActivity.this);
                builder.setTitle("选项");
                builder.setItems(R.array.add_phone_to_dark_button_choice, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which){
                        if(which==0){//手动添加号码
                            editText=new EditText(MessageDarkListActivity.this);
                            editText.setHint("输入11位手机号码");//提示信息
                            editText.setMaxLines(1);//最大行数editText
                            new AlertDialog.Builder(MessageDarkListActivity.this).setTitle("请输入号码")
                                    .setView(editText)
                                    .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialog,int which){
                                            phoneNum=editText.getText().toString();
                                            if(phoneNum!=null&& Common.isMobile(phoneNum)&&!phoneInDarkList(phoneNum)){//保存到库表中
                                                MessageDarkListHarass messageDarkListHarass=new MessageDarkListHarass();
                                                messageDarkListHarass.setPhoneNum(phoneNum);
                                                messageDarkListHarass.save();
                                                new Thread(runable).start();
                                                Toast.makeText(MessageDarkListActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                                            }else if(phoneInDarkList(phoneNum)&&phoneNum!=null&&Common.isMobile(phoneNum)){//已在黑名单中
                                                Toast.makeText(MessageDarkListActivity.this,"号码已在黑名单中",Toast.LENGTH_SHORT).show();

                                            }
                                            else{
                                                Toast.makeText(MessageDarkListActivity.this,"请输入正确号码",Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                    }).setNegativeButton("取消",null).show();


                        }else if(which==1){//从联系人添加
                            Intent intent=new Intent(MessageDarkListActivity.this,MessageDarkContactActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        dialog.dismiss();//关窗

                    }
                });
                dialog=builder.create();
                dialog.show();
            }

        });

    }





    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            listHarasses=getMessageDarkListHarrass();
            messageDarkArrayList=new ArrayList<String>();
            for(MessageDarkListHarass messageDarkListHarass:listHarasses){
                messageDarkArrayList.add(messageDarkListHarass.getPhoneNum());
            }
            myHandler.obtainMessage().sendToTarget();

        }
    };

    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            if(messageDarkArrayList.size()==0){
                messageDarkStateView.setVisibility(View.VISIBLE);//可见
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(MessageDarkListActivity.this,android.R.layout.simple_list_item_1,messageDarkArrayList);
                messageDarkList.setAdapter(arrayAdapter);
            }else{
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(MessageDarkListActivity.this,android.R.layout.simple_list_item_1,messageDarkArrayList);
                messageDarkList.setAdapter(arrayAdapter);

            }

        }

    };

    /**
     * 获得所有短信黑名单
     * @return
     */
    public List<MessageDarkListHarass> getMessageDarkListHarrass(){
        List<MessageDarkListHarass> listHarasses= DataSupport.findAll(MessageDarkListHarass.class);
        return listHarasses;
    }

    /**
     * 是否已在黑名单中
     * @param phoneNum
     * @return
     */
    public boolean phoneInDarkList(String phoneNum){
        boolean in=false;
        List<MessageDarkListHarass> lists=getMessageDarkListHarrass();
        for(MessageDarkListHarass list:lists){
            if(phoneNum.equals(list.getPhoneNum())){
                in=true;
                return in;
            }
        }
        return in;

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
