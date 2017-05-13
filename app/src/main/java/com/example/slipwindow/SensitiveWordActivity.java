package com.example.slipwindow;
/**
 * 列出关键词黑名单
 */
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.slipwindow.Adapter.AppAdapter;
import com.example.slipwindow.db.MessageDarkListHarass;
import com.example.slipwindow.db.PhoneHarrassRecorder;
import com.example.slipwindow.util.Common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SensitiveWordActivity extends AppCompatActivity {
    private ArrayList<String> sensitiveWords=new ArrayList<>();//读取所有关键词
    private TextView stateText;
    private ArrayAdapter<String> wordAdapter;
    private FloatingActionButton floatingActionButton;
    private ListView wordList;
    private AlertDialog alertDialog;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("关键词黑名单");
        setContentView(R.layout.activity_sensitive_word);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.add_new_sensitive_word_to_list_button);
        stateText=(TextView)findViewById(R.id.sensitive_word_list_state_view);
        wordList=(ListView)findViewById(R.id.sensitive_word_list);
        new Thread(runable).start();
        wordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(SensitiveWordActivity.this);
                builder.setTitle("操作");
                builder.setItems(R.array.sensitive_item_handle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==1){//删除
                            sensitiveWords.remove(position);
                            setTextState(sensitiveWords);//为空时提示无拦截信息
                            changeSensitiveWords(sensitiveWords);//修改文件信息
                           /* for(String word:sensitiveWords){
                                changeSensitiveWords(word);//修改文件信息
                            }*/
                            wordAdapter.notifyDataSetChanged();
                            Toast.makeText(SensitiveWordActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                        }else if(which==0){//编辑
                            final String word=sensitiveWords.get(position);//获取选中关键词
                            editText=new EditText(SensitiveWordActivity.this);
                            editText.setText(word);//提示信息
                            editText.setMaxLines(1);//最大行数
                            new AlertDialog.Builder(SensitiveWordActivity.this).setTitle("编辑")
                                    .setView(editText)
                                    .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialog,int which){
                                            String newWord=editText.getText().toString();
                                            if(!newWord.equals("")||newWord!=null){
                                                if(!newWord.equals(word)){//若关键字变化
                                                    sensitiveWords.set(position,newWord);
                                                /*for(String word:sensitiveWords){
                                                    changeSensitiveWords(word);//修改文件信息
                                                }*/
                                                    changeSensitiveWords(sensitiveWords);//修改文件信息
                                                    wordAdapter.notifyDataSetChanged();
                                                    Toast.makeText(SensitiveWordActivity.this,"编辑成功",Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                Toast.makeText(SensitiveWordActivity.this,"内容不能为空",Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                    }).setNegativeButton("取消",null).show();

                        }
                        dialog.dismiss();//关闭提示框
                    }
                });
                alertDialog=builder.create();
                alertDialog.show();
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener(){//添加
            public void onClick(View view){
                final EditText editText1=new EditText(SensitiveWordActivity.this);
                editText1.setHint("请输入关键字");//提示信息
                editText1.setMaxLines(1);//最大行数
                new AlertDialog.Builder(SensitiveWordActivity.this).setTitle("添加")
                        .setView(editText1)
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog,int which){
                                String newWord=editText1.getText().toString();
                                if(!newWord.equals("")||newWord!=null){
                                    if(!isExit(newWord)){
                                        sensitiveWords.add(newWord);
                                    /*for(String word:sensitiveWords){
                                        changeSensitiveWords();//修改文件信息
                                    }*/
                                        changeSensitiveWords(sensitiveWords);//修改文件信息
                                        wordAdapter.notifyDataSetChanged();
                                        setTextState(sensitiveWords);
                                        Toast.makeText(SensitiveWordActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(SensitiveWordActivity.this,"关键词已存在",Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(SensitiveWordActivity.this,"内容不能为空",Toast.LENGTH_SHORT).show();
                                }

                            }

                        }).setNegativeButton("取消",null).show();
            }
        });

    }


    final Runnable runable=new Runnable() {
        @Override
        public void run() {
            SharedPreferences pre=getSharedPreferences("phoneModle",MODE_PRIVATE);
            if(!pre.getBoolean("sensitiveWord",false)){//是否有关键词文件
                SharedPreferences.Editor editor=pre.edit();
                changeSensitiveWords(sensitiveWords);//创建表
                editor.putBoolean("sensitiveWord",true);
                editor.apply();
            }else{
                getAllSensitiveWord();//读取所有关键字
            }
            myHandler.obtainMessage().sendToTarget();

        }
    };
    private Handler myHandler=new Handler() {
        public void handleMessage(Message msg){
            wordAdapter=new ArrayAdapter<String>(SensitiveWordActivity.this,android.R.layout.simple_list_item_1,sensitiveWords);
            setTextState(sensitiveWords);
            wordList.setAdapter(wordAdapter);//适配
        }

    };


    /**
     * 加载Menu布局
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.phone_harrass_record,menu);
        return true;
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
        }else if(item.getItemId()==R.id.delete_all_phone_harrass){//右上角清空键
            sensitiveWords.clear();
            changeSensitiveWords(sensitiveWords);//清空文件信息
            setTextState(sensitiveWords);//为空时提示无拦截信息
            wordAdapter.notifyDataSetChanged();
            Toast.makeText(SensitiveWordActivity.this,"已清空",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 将关键字读入数组sensitiveWords
     */
    public void getAllSensitiveWord(){
        FileInputStream in=null;
        BufferedReader reader=null;
        try{
            in=openFileInput("sensitiveWord");//打开表，表中一个关键字一行
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while ((line=reader.readLine())!=null){//读取一行
                sensitiveWords.add(line);//存入数组
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try{
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }


    public void createWordFile(){


    }

    /**
     * 状态信息
     * @param sensitiveWords
     */
    public void setTextState(ArrayList<String> sensitiveWords){

        if(sensitiveWords.size()>0){
            stateText.setVisibility(View.GONE);
        }else{
            stateText.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 修改关键词黑名单文件内容，即设为空、删除及添加关键字
     */
    public void changeSensitiveWords(ArrayList<String> words){
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try{
            out=openFileOutput("sensitiveWord",MODE_PRIVATE);//MODE_PRIVATE无文件，则创建；有文件，则覆盖文件内容
            writer=new BufferedWriter(new OutputStreamWriter(out));
            if(words.size()>0){
                for(String word:words){
                    writer.write(word);
                    writer.newLine();
                    // writer.flush();//
                    // writer.newLine();//换行
                }
            }else{
                writer.write("");
            }

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(writer!=null){
                    writer.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 关键字是否重复
     * @param newWord
     * @return
     */
    public boolean isExit(String newWord){
        boolean is=false;
        for(String word:sensitiveWords){
            if(word.equals(newWord)){
                is=true;
                return is;
            }
        }
        return is;
    }



}
