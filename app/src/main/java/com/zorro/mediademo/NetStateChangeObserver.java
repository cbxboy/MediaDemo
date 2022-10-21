package com.zorro.mediademo;

import androidx.lifecycle.Observer;

/**
 * @Author : cbx
 * @Email : 673591077@qq.com
 * @Date : on 2022-10-20 17:10.
 * @Description :描述
 */
public interface NetStateChangeObserver {


    void onNetDisconnected();
    void onNetConnected(NetworkType networkType);
}
