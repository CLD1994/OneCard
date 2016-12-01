package com.zhuoli.onecard.setting.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhuoli.onecard.R;
import com.zhuoli.onecard.setting.adapter.model.PrintContent;

import java.util.List;

/**
 * author: CLD
 * created on: 2016/10/28 0028 下午 1:14
 * description:
 */

public class PrintAdapter extends BaseMultiItemQuickAdapter<PrintContent,BaseViewHolder>{

	public PrintAdapter(List<PrintContent> data) {
		super(data);
		addItemType(PrintContent.SINGLE, R.layout.item_print_single);
		addItemType(PrintContent.BOTH,R.layout.item_print_both);
	}

	@Override
	protected void convert(BaseViewHolder baseViewHolder, final PrintContent printContent) {
		EditText et_print_single = baseViewHolder.getView(R.id.et_print_single);
		et_print_single.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				printContent.setSingleContent(s.toString());
			}
		});
		et_print_single.setText(printContent.getSingleContent());
		switch (baseViewHolder.getItemViewType()){
			case PrintContent.SINGLE:
				break;
			case PrintContent.BOTH:
				EditText et_print_both = baseViewHolder.getView(R.id.et_print_both);
				et_print_both.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {

					}

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {

					}

					@Override
					public void afterTextChanged(Editable s) {
						printContent.setBothContent(s.toString());
					}
				});
				et_print_both.setText(printContent.getBothContent());
				break;
		}
		baseViewHolder.addOnClickListener(R.id.btn_clear);
	}
}
