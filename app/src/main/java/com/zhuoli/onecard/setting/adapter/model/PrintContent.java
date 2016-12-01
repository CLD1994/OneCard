package com.zhuoli.onecard.setting.adapter.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * author: CLD
 * created on: 2016/10/28 0028 下午 1:05
 * description:
 */

public class PrintContent implements MultiItemEntity {

	public static final int SINGLE = 0;
	public static final int BOTH = 1;
	private int itemType;
	private String singleContent;
	private String bothContent;

	public PrintContent(int itemType, String singleContent, String bothContent){
		this.itemType = itemType;
		this.singleContent = singleContent;
		this.bothContent = bothContent;
	}

	public PrintContent(int itemType, String singleContent){
		this.itemType = itemType;
		this.singleContent = singleContent;
	}

	@Override
	public int getItemType() {
		return itemType;
	}

	public String getSingleContent() {
		return singleContent;
	}

	public String getBothContent() {
		return bothContent;
	}

	public void setSingleContent(String singleContent) {
		this.singleContent = singleContent;
	}

	public void setBothContent(String bothContent) {
		this.bothContent = bothContent;
	}
}
