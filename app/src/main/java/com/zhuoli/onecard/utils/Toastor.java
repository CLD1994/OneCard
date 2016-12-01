package com.zhuoli.onecard.utils;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 赵晨璞 on 2016/8/11.
 * Modify by cld on 2016/8/29 0029 下午 6:26.
 */
public class Toastor {

    private Context mContext;
    private Toast mToast;
    private LinearLayout toastView;

    public Toastor(Context context){
        mContext = context;
    }


    public void showToast(int resId) {
        if (mToast == null||(toastView!=null&&toastView.getChildCount()>1)) {
            mToast = Toast.makeText(mContext, resId, Toast.LENGTH_SHORT);
            toastView=null;
        }else{
            mToast.setText(resId);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public void showToast(String text) {
        if (mToast == null||(toastView!=null&&toastView.getChildCount()>1)) {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
            toastView=null;
        }else{
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public void showLongToast(int resId) {
        if (mToast == null||(toastView!=null&&toastView.getChildCount()>1)) {
            mToast = Toast.makeText(mContext, resId, Toast.LENGTH_LONG);
            toastView=null;
        }else{
            mToast.setText(resId);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }

    public void showLongToast(String text) {
        if (mToast == null||(toastView!=null&&toastView.getChildCount()>1)) {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
            toastView=null;
        }else{
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }

    /**
     * 向Toast中添加自定义view
     * @param view
     * @param postion
     * @return
     */
    public void addView(View view,int postion) {
        toastView = (LinearLayout) mToast.getView();
        toastView.addView(view, postion);
    }

    /**
     * 设置Toast字体及背景颜色
     * @param messageColor
     * @param backgroundColor
     * @return
     */
    public void setToastColor(int messageColor, int backgroundColor) {
        View view = mToast.getView();
        if(view!=null){
            TextView message=((TextView) view.findViewById(android.R.id.message));
            message.setBackgroundColor(backgroundColor);
            message.setTextColor(messageColor);
        }
    }

    /**
     * 设置Toast字体及背景
     * @param messageColor
     * @param background
     * @return
     */
    public void setToastBackground(int messageColor, int background) {
        View view = mToast.getView();
        if(view!=null){
            TextView message=((TextView) view.findViewById(android.R.id.message));
            message.setBackgroundResource(background);
            message.setTextColor(messageColor);
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public void Indefinite(Context context, CharSequence message, int duration) {
        if(mToast==null||(toastView!=null&&toastView.getChildCount()>1)){
            mToast= Toast.makeText(context, message,duration);
            toastView=null;
        }else{
            mToast.setText(message);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

}
