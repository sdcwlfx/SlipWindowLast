package com.example.slipwindow.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by asus on 2017-04-25.
 */

public class SensitiveWordInit {
    private String ENCODING="GBK";//字符编码

    public HashMap sensitiveWordMap;//存储敏感词库

    public SensitiveWordInit(){
        super();
    }

    public Map initKeyWord(Context context){
        try{
            //读取敏感词库
            Set<String> keyWordSet=readSensitiveWordFile(context);//存储拦截关键字
            //将敏感词库加入HashMap中
            addSensitiveWordToHashMap(keyWordSet);
        }catch (Exception e){
            e.printStackTrace();
        }
        return sensitiveWordMap;
    }

    /**
     * 将敏感词库加入HashMap中
     * @param keyWordSet
     */
    private void addSensitiveWordToHashMap(Set<String> keyWordSet){
        sensitiveWordMap = new HashMap(keyWordSet.size());////初始化敏感词容器，减少扩容操作
        String key = null;
        Map nowMap = null;
        Map<String, String> newWorMap = null;
        //迭代keyWordSet
        Iterator<String> iterator = keyWordSet.iterator();
        while(iterator.hasNext()){
            key = iterator.next();    //关键字
            nowMap = sensitiveWordMap;
            for(int i = 0 ; i < key.length() ; i++){
                char keyChar = key.charAt(i);       //转换成char型
                Object wordMap = nowMap.get(keyChar);       //获取

                if(wordMap != null){        //如果存在该key，直接赋值
                    nowMap = (Map) wordMap;
                }
                else{     //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                    newWorMap = new HashMap<String,String>();
                    newWorMap.put("isEnd","0");//不是最后一个
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }

                if(i == key.length() - 1){
                    nowMap.put("isEnd", "1");    //最后一个
                }
            }
        }


    }


    private Set<String> readSensitiveWordFile(Context context) throws Exception{
        Set<String> set=null;
        FileInputStream in=null;
        BufferedReader bufferedReader=null;
        try{
            in= context.openFileInput("sensitiveWord");//文件名
            InputStreamReader read = new InputStreamReader(in);
            set=new HashSet<String>();
             bufferedReader = new BufferedReader(read);
            String txt = null;
            while((txt = bufferedReader.readLine()) != null){    //读取文件，将文件内容放入到set中
                    set.add(txt);
            }
    }catch (Exception e){
           e.printStackTrace();
        }finally {
            if(bufferedReader!=null){
                try{
                    bufferedReader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return set;
        }
}
