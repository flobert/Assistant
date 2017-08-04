package com.colaapk.assistant;

/**
 * Created by LLY on 2017/8/4.
 */

public class Result {

    /**
     * errcode : 0
     * msg :
     * code : 0
     * messsage : 数据提交成功！
     */

    private int errcode;
    private String msg;
    private int code;
    private String messsage;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMesssage() {
        return messsage;
    }

    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }
}
