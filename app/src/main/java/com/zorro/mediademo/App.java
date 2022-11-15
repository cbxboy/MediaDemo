package com.zorro.mediademo;

import android.app.Application;

/**
 * @Author : cbx
 * @Email : 673591077@qq.com
 * @Date : on 2022-11-14 14:21.
 * @Description :描述
 */
public class App extends Application {


    private static FloatWindowService mFloatWindowService;

    public static FloatWindowService getFloatWindowService() {
        return mFloatWindowService;
    }

    public static void setFloatWindowService(FloatWindowService floatWindowService) {
        mFloatWindowService = floatWindowService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
