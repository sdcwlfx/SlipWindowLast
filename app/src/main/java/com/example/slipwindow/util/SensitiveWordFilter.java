package com.example.slipwindow.util;

/**
 * 关键词过滤
 */

import android.content.Context;

import java.util.Map;

/**
 * Created by asus on 2017-04-25.
 */

public class SensitiveWordFilter {
    public Map sensitiveWordMap = null;
    public static int minMatchTYpe = 1;      //最小匹配规则
    public static int maxMatchType = 2;      //最大匹配规则

    //初始化关键词词库
    public SensitiveWordFilter(Context context){
        sensitiveWordMap=new SensitiveWordInit().initKeyWord(context);
    }

    /**
     * 是否包含过滤关键字
     * @param txt
     * @param matchType
     * @return
     */
    public boolean isContainSensitiveWord(String txt,int matchType){
        boolean flag=false;
        for(int i=0;i<txt.length();i++){
            int matchFlag=this.CheckSensitiveWord(txt,i,matchType);//判断是否包含关键字
            if(matchFlag>0){
                flag=true;
            }
        }
        return flag;
    }

    public int CheckSensitiveWord(String txt,int beginIndex,int matchType){
        boolean flag=false;//敏感词结束标识位：用于敏感词只有1位的情况
        int matchFlag=0;//匹配标识数默认为0
        char word=0;
        Map nowMap=sensitiveWordMap;
        for(int i=beginIndex;i<txt.length();i++){
            word=txt.charAt(i);
            nowMap=(Map)nowMap.get(word);//获取指定key
            if(nowMap!=null){//存在，则判断是否为最后一个
                matchFlag++;//找到相应key，匹配标识+1
                if("1".equals(nowMap.get("isEnd"))){//如果为最后一个匹配规则,结束循环，返回匹配标识数
                    flag=true;//结束标志位为true
                    if(SensitiveWordFilter.minMatchTYpe==matchType){//最小规则，直接返回,最大规则还需继续查找
                        break;
                    }
                }
            }else{//不存在，直接返回
                break;
            }
        }
        if(matchFlag<2||!flag){//长度必须大于等于1，为词
            matchFlag=0;
        }
        return matchFlag;

    }



}
