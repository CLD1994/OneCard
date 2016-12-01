package com.zhuoli.onecard.setting.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhuoli.onecard.R;

import java.util.List;

import codetail.graphics.drawables.DrawableHotspotTouch;
import codetail.graphics.drawables.LollipopDrawable;
import codetail.graphics.drawables.LollipopDrawablesCompat;

/**
 * author: CLD
 * created on: 2016/11/28 0028 下午 9:18
 * description:
 */

public class CleanAdapter extends BaseQuickAdapter<String,BaseViewHolder>{

	private final Context mContext;

	public CleanAdapter(int layoutResId, List<String> data, Context context) {
		super(layoutResId, data);
		mContext = context;
	}

	@Override
	protected void convert(BaseViewHolder baseViewHolder, String s) {
		baseViewHolder.setText(R.id.btn_clear,s);
		baseViewHolder.setTextColor(R.id.btn_clear, ContextCompat.getColor(mContext,R.color.white));
		Button btn_clear = baseViewHolder.getView(R.id.btn_clear);
		btn_clear.setBackground(LollipopDrawablesCompat.getDrawable(mContext.getResources(), R.drawable.bg_btn, mContext.getTheme()));
		btn_clear.setOnTouchListener(new DrawableHotspotTouch((LollipopDrawable) btn_clear.getBackground()));
	}
}
