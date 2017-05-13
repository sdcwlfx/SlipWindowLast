package com.example.slipwindow;
/**
 * 短信白名单列表
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slipwindow.db.MessageDarkListHarass;
import com.example.slipwindow.db.MessageWhiteListPass;
import com.example.slipwindow.util.Common;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MessageWhiteListActivity extends AppCompatActivity {

    private ListView messageWhiteList;
    private FloatingActionButton floatingActionButton;//悬浮按钮
    private TextView messageWhiteStateView;
    private ArrayList<String> messageWhiteArrayList;
    private EditText editText;
    private String phoneNum;
    private AlertDialog dialog;
    private AlertDialog alertDialog;
    private List<MessageWhiteListPass> listPasses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("短信白名单");
        setContentView(R.layout.activity_message_white_list);
        messageWhiteList=(ListView)findViewById(R.id.message_white_list);
        messageWhiteStateView=(TextView)findViewById(R.id.message_white_list_state_view);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.add_new_message_to_white_list_button);//添加按钮
        new Thread(runable).start();//开启线程
        //黑名单列表点击事件
        messageWhiteList.setOnItemClickListener(new AdapterView.OnItemClickListener(){//从黑名单删除
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
                AlertDialog.Builder builder=new AlertDialog.Builder(MessageWhiteListActivity.this);
                builder.setTitle("操作");
                builder.setItems(R.array.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            MessageWhiteListPass pass=listPasses.get(position);
                            pass.delete();
                            new Thread(runable).start();//重新加载适配器
                            Toast.makeText(MessageWhiteListActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder builder=new AlertDialog.Builder(MessageWhiteListActivity.this);
                builder.setTitle("选项");
                builder.setItems(R.array.add_phone_to_dark_button_choice, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which){
                        if(which==0){//手动添加号码
                            editText=new EditText(MessageWhiteListActivity.this);
                            editText.setHint("输入11位手机号码");//提示信息
                            editText.setMaxLines(1);//最大行数
                            new AlertDialog.Builder(MessageWhiteListActivity.this).setTitle("请输入号码")
                                    .setView(editText)
                                    .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialog,int which){
                                            phoneNum=editText.getText().toString();
                                            if(phoneNum!=null&& Common.isMobile(phoneNum)&&!phoneInWhiteList(phoneNum)){//保存到库表中
                                                MessageWhiteListPass messageWhiteListPass1=new MessageWhiteListPass();
                                                messageWhiteListPass1.setPhone(phoneNum);
                                                messageWhiteListPass1.save();
                                                new Thread(runable).start();
                                                Toast.makeText(MessageWhiteListActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                                            }else if(phoneInWhiteList(phoneNum)&&phoneNum!=null&&Common.isMobile(phoneNum)){//已在黑名单中
                                                Toast.makeText(MessageWhiteListActivity.this,"号码已在黑名单中",Toast.LENGTH_SHORT).show();

                                            }
                                            else{
                                                Toast.makeText(MessageWhiteListActivity.this,"请输入正确号码",Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                    }).setNegativeButton("取消",null).show();


                        }else if(which==1){//从联系人添加
                            Intent intent=new Intent(MessageWhiteListActivity.this,MessageWhiteContactActivity.class);
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
            listPasses=getMessageWhiteListPass();
            messageWhiteArrayList=new ArrayList<String>();
            for(MessageWhiteListPass messageWhiteListPass:listPasses){
                messageWhiteArrayList.add(messageWhiteListPass.getPhone());
            }
            myHandler.obtainMessage().sendToTarget();

        }
    };

    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            if(messageWhiteArrayList.size()==0){
                messageWhiteStateView.setVisibility(View.VISIBLE);//可见
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(MessageWhiteListActivity.this,android.R.layout.simple_list_item_1,messageWhiteArrayList);
                messageWhiteList.setAdapter(arrayAdapter);
            }else{
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(MessageWhiteListActivity.this,android.R.layout.simple_list_item_1,messageWhiteArrayList);
                messageWhiteList.setAdapter(arrayAdapter);

            }

        }

    };

    /**
     * 获得所有短信白名单
     * @return
     */
    public List<MessageWhiteListPass> getMessageWhiteListPass(){
        List<MessageWhiteListPass> listHarasses= DataSupport.findAll(MessageWhiteListPass.class);
        return listHarasses;
    }

    /**
     * 是否已在短信白名单中
     * @param phoneNum
     * @return
     */
    public boolean phoneInWhiteList(String phoneNum){
        boolean in=false;
        List<MessageWhiteListPass> lists=getMessageWhiteListPass();
        for(MessageWhiteListPass list:lists){
            if(phoneNum.equals(list.getPhone())){
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
