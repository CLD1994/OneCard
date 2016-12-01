package com.zhuoli.onecard.main.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhuoli.onecard.R;
import com.zhuoli.onecard.main.adapter.model.Option;

import java.util.List;

/**
 * Created by CLD on 2016/9/26 0026.
 */

public class OptionAdapter extends BaseQuickAdapter<Option, BaseViewHolder> {

	public OptionAdapter(int layoutResId, List<Option> data) {
		super(layoutResId, data);
	}

	@Override
	protected void convert(BaseViewHolder baseViewHolder, Option option) {
		baseViewHolder.setText(R.id.btn_option,option.getName());
	}
}
