package com.example.slipwindow.util;

import java.text.DecimalFormat;

/**
 * Created by asus on 2017-04-26.
 */

public class TextFormat {
    public static String formatByte(long data){
        DecimalFormat format=new DecimalFormat();
        if(data<1024){
            return data+"bytes";
        }else if(data<1024*1024){
            return format.format(data/1024f)+"KB";
        }else if(data<1024*1024*1024){
            return format.format(data/1024f/1024f)+"MB";
        }else if(data<1024*1024*1024*1024){
            return format.format(data/1024f/1024f/1024f)+"GB";
        }else {
            return "超出统计范围";
        }
    }

    public static float formatToMbFromByte(long data){
        DecimalFormat format=new DecimalFormat();
            return(data/1024f/1024f);

    }


}
