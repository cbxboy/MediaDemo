package com.zorro.mediademo;

/**
 * @Author : cbx
 * @Email : 673591077@qq.com
 * @Date : on 2022-10-20 17:07.
 * @Description :描述
 */
public enum  NetworkType {
    NETWORK_WIFI("WiFi"),
    NETWORK_4G("4G"),
    NETWORK_2G("2G"),
    NETWORK_3G("3G"),
    NETWORK_UNKNOWN("Unknown"),
    NETWORK_NO("No network");

    private String desc;
    NetworkType(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}
