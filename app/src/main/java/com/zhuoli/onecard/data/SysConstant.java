package com.zhuoli.onecard.data;

/**
 * Created by CLD on 2016/8/4 0004.
 */
public class SysConstant {

	public static final String OK = "OK";
	public static final int RETRY = 3;
	public static final int RETRY_INTERVAL = 1000;

	//命令格式常量
	public static final String FRAME_HEADER = "55";
	public static final String FRAME_FOOTER = "AA";
	public static final String TAGGED_WORD = "020000";
	public static final String ADDRESS = "FF";

	//参数命令
	public static final String WRITE_PARAMETER = "SP";
	public static final String READ_PARAMETER = "GP";
	//地址命令
	public static final String WRITE_ADDRESS = "SA";
	public static final String READ_ADDRESS = "RA";
	//打印内容命令
	public static final String WRITE_CONTENT = "SN";
	public static final String READ_CONTENT = "PN";
	//清洗喷头命令
	public static final String CLEAN_SPRAYER = "CL";
	public static final String CLEAN_SPRAYER_SINGLE = "CN";
	public static final String STOP_CLEAN_SPRAYER = "SO";
	//设置系统时钟命令
	public static final String WRITE_SYSTEM_TIME = "ST";
	public static final String READ_SYSTEM_TIME = "RT";
	//喷印序号命令
	public static final String WRITE_SPRAY_SERIAL = "SL";
	public static final String READ_SPRAY_SERIAL = "RS";
	//喷印计数器命令
	public static final String WRITE_COUNTER_CONFIG = "SC";
	public static final String READ_COUNTER_CONFIG = "RC";
}
