package com.zhuoli.onecard.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;

/**
 * Created by WJK on 2016/7/27.
 */
public class TextViewUtil {

    //给文字添加删除线
    public static SpannableString lineText(String str){
        SpannableString spannableString = new SpannableString(str);
        StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
        spannableString.setSpan(strikethroughSpan, 0,str.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    //缩小文字的部分
    public static SpannableString zoomText(String str,int start,int end){
        SpannableString spannableString = new SpannableString(str);
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.7f);
        spannableString.setSpan(sizeSpan, start,end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    //改变部分文字颜色
    public static SpannableString colorText(String str,int start,int end){
        SpannableString spannableString = new SpannableString(str);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#fa565a"));
        spannableString.setSpan(colorSpan, start,end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

}
