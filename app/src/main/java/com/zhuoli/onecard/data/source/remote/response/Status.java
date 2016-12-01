package com.zhuoli.onecard.data.source.remote.response;

/**
 * Created by CLD on 2016/7/27.
 */
public class Status {

    public static final String NETWORK_ERROR = "-1";

    public static final String OK = "0000";

    public static final String SYSTEM_ERROR = "1001";

    public static final String MISSING_VERSION = "2030";

    public static final String ILLEGAL_CLIENT = "2009";

    public static final String NO_UPDATE = "2012";

    public static final String USERNAME_NOT_EXIST = "2010";

    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Status() {
    }

    public Status(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Status{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
