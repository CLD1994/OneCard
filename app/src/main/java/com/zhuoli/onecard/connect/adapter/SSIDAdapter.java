package com.zhuoli.onecard.connect.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhuoli.onecard.R;
import com.zhuoli.onecard.data.model.SSID;

import java.util.List;

/**
 * author: CLD
 * created on: 2016/10/24 0024 下午 4:28
 * description:
 */

public class SSIDAdapter extends BaseQuickAdapter<SSID,BaseViewHolder>{

	public SSIDAdapter(int layoutResId, List<SSID> data) {
		super(layoutResId, data);
	}

	@Override
	protected void convert(BaseViewHolder baseViewHolder, SSID ssid) {
		baseViewHolder.setText(R.id.ssid,ssid.getName());
		baseViewHolder.addOnClickListener(R.id.ssid);
	}

}
