package com.zhuoli.onecard.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * author: CLD
 * created on: 2016/10/26 0026 下午 10:05
 * description:
 */

public class SSID extends RealmObject{
	@PrimaryKey
	private String ssid;

	private String name;

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "SSID{" +
				"ssid='" + ssid + '\'' +
				", name='" + name + '\'' +
				'}';
	}
}
