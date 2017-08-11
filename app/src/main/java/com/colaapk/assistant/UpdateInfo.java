package com.colaapk.assistant;

/**
 * Created by LLY on 2017/8/11.
 */

public class UpdateInfo {

    /**
     * name : 微Q改步器
     * versionCode : 1
     * versionName : 0.0.2
     * instruction : 优化代码结构,增加春雨无用代码清理功能，增加在线升级功能
     * url : xxxxxxxxxxx
     */

    private String name;
    private String versionCode;
    private String versionName;
    private String instruction;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
