package com.example.slipwindow;

/**
 * 来电黑名单列表
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

import com.example.slipwindow.db.PhoneDarkListHarass;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class PhoneDarkListActivity extends AppCompatActivity {

    private ListView phoneDarkList;
    private FloatingActionButton floatingActionButton;//悬浮按钮
    private TextView phoneDarkStateView;
    private ArrayList<String> phoneDarkArrayList;
    private EditText editText;
    private String phoneNum;
    private AlertDialog dialog;
    private AlertDialog alertDialog;
    private List<PhoneDarkListHarass> listHarasses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("来电黑名单");
        setContentView(R.layout.activity_phone_dark_list);
        phoneDarkList=(ListView)findViewById(R.id.phone_dark_list);
        phoneDarkStateView=(TextView)findViewById(R.id.phone_dark_list_state_view);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.add_new_phone_to_dark_list_button);//添加按钮
        new Thread(runable).start();//开启线程
        //黑名单列表点击事件
        phoneDarkList.setOnItemClickListener(new AdapterView.OnItemClickListener(){//从黑名单删除
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
                AlertDialog.Builder builder=new AlertDialog.Builder(PhoneDarkListActivity.this);
                builder.setTitle("操作");
                builder.setItems(R.array.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0){
                                    PhoneDarkListHarass harass=listHarasses.get(position);
                                    harass.delete();
                                    new Thread(runable).start();//重新加载适配器
                                    Toast.makeText(PhoneDarkListActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder builder=new AlertDialog.Builder(PhoneDarkListActivity.this);
                builder.setTitle("选项");
                builder.setItems(R.array.add_phone_to_dark_button_choice, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which){
                        if(which==0){//手动添加号码
                            editText=new EditText(PhoneDarkListActivity.this);
                            editText.setHint("输入11位手机号码");//提示信息
                            editText.setMaxLines(1);//最大行数
                            new AlertDialog.Builder(PhoneDarkListActivity.this).setTitle("请输入号码")
                                    .setView(editText)
                                    .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialog,int which){
                                            phoneNum=editText.getText().toString();
                                            if(phoneNum!=null&&isMobile(phoneNum)&&!phoneInDarkList(phoneNum)){//保存到库表中
                                                PhoneDarkListHarass phoneDarkListHarass=new PhoneDarkListHarass();
                                                phoneDarkListHarass.setPhoneNum(phoneNum);
                                                phoneDarkListHarass.save();
                                                new Thread(runable).start();
                                                Toast.makeText(PhoneDarkListActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                                            }else if(phoneInDarkList(phoneNum)&&phoneNum!=null&&isMobile(phoneNum)){//已在黑名单中
                                                Toast.makeText(PhoneDarkListActivity.this,"号码已在黑名单中",Toast.LENGTH_SHORT).show();

                                            }
                                            else{
                                                Toast.makeText(PhoneDarkListActivity.this,"请输入正确号码",Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                    }).setNegativeButton("取消",null).show();


                        }else if(which==1){//从联系人添加
                            Intent intent=new Intent(PhoneDarkListActivity.this,PhoneDarkContactActivity.class);
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


    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            listHarasses=getPhoneDarkListHarrass();
            phoneDarkArrayList=new ArrayList<String>();
            for(PhoneDarkListHarass phoneDarkListHarass:listHarasses){
                phoneDarkArrayList.add(phoneDarkListHarass.getPhoneNum());
            }
            myHandler.obtainMessage().sendToTarget();

        }
    };

    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            if(phoneDarkArrayList.size()==0){
                phoneDarkStateView.setVisibility(View.VISIBLE);//可见
               ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(PhoneDarkListActivity.this,android.R.layout.simple_list_item_1,phoneDarkArrayList);
               phoneDarkList.setAdapter(arrayAdapter);
            }else{
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(PhoneDarkListActivity.this,android.R.layout.simple_list_item_1,phoneDarkArrayList);
                phoneDarkList.setAdapter(arrayAdapter);

            }

        }

    };

    /**
     * 获得所有电话黑名单
     * @return
     */
    public List<PhoneDarkListHarass> getPhoneDarkListHarrass(){
        List<PhoneDarkListHarass> listHarasses= DataSupport.findAll(PhoneDarkListHarass.class);
        return listHarasses;
    }

    /**
     * 判定电话号码
     * @param num
     * @return
     */
    public static boolean isMobile(String num){
        String telRegex="[1][358]\\d{9}";
        if(TextUtils.isEmpty(num)){
            return false;
        }else{
            return num.matches(telRegex);
        }
    }

    /**
     * 是否已在黑名单中
     * @param phoneNum
     * @return
     */
    public boolean phoneInDarkList(String phoneNum){
        boolean in=false;
        List<PhoneDarkListHarass> lists=getPhoneDarkListHarrass();
        for(PhoneDarkListHarass list:lists){
            if(phoneNum.equals(list.getPhoneNum())){
                in=true;
                return in;
            }
        }
        return in;

    }
}
