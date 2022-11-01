package com.zorro.mediademo;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * @Author : cbx
 * @Email : 673591077@qq.com
 * @Date : on 2022-10-31 17:26.
 * @Description :描述
 */
public class MyServiceConnection implements ServiceConnection {

    private MqttService mqttService;
    private IGetMessageCallBack iGetMessageCallBack;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mqttService =  ((MqttService.CustomBinder)service).getService();
        mqttService.setIGetMessageCallBack(iGetMessageCallBack);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public MqttService getMqttService() {
        return mqttService;
    }

    public void setIGetMessageCallBack(IGetMessageCallBack iGetMessageCallBack) {
        this.iGetMessageCallBack = iGetMessageCallBack;
    }
}
