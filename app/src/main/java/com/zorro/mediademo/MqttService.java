package com.zorro.mediademo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttService extends Service {

    public static final String TAG = MqttService.class.getSimpleName();

    private static MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    private IGetMessageCallBack mIGetMessageCallBack;

    private String host = "tcp://210.73.216.2:1883";
    private String userName = "admin";
    private String passWord = "password";
    private String device_id;
    private static String store_id;
    private String clientId = "AndroidClient_";

    public MqttService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        device_id = SharePreferenceUtils.getString(this, "device_id");
        store_id = SharePreferenceUtils.getString(this, "store_id");
        clientId = clientId + device_id;
        client = new MqttAndroidClient(this, host, clientId);
        conOpt = new MqttConnectOptions();
        client.setCallback(mqttCallback);
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(20);
        conOpt.setUserName(userName);
        conOpt.setPassword(passWord.toCharArray());
        doClientConnection();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new CustomBinder();
    }

    public class CustomBinder extends Binder {
        public MqttService getService(){
            return MqttService.this;
        }
    }

    public void setIGetMessageCallBack(IGetMessageCallBack iGetMessageCallBack) {
        this.mIGetMessageCallBack = iGetMessageCallBack;
    }

    /** 连接MQTT服务器 */
    private void doClientConnection() {
        if (!client.isConnected() && isConnectIsNormal()) {
            try {
                client.connect(conOpt, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    public static void publishMessage(String payload) {
        String topic = store_id;
        try {
            if (client.isConnected() == false) {
                client.connect();
            }

            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            message.setQos(0);
            client.publish(topic, message,null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "publish succeed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "publish failed!");
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    // MQTT是否连接成功
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.d(TAG, "onSuccess: 连接成功");
            try {
                //连接成功后订阅主题
                client.subscribe(store_id, 2);

            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            exception.printStackTrace();
        }
    };

    // MQTT监听并且接受消息
    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            Log.d(TAG, "connectionLost: 连接断开");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String str = new String(message.getPayload());
            if (mIGetMessageCallBack != null){
                mIGetMessageCallBack.setMessage(str);
            }
            Log.d(TAG, "消息到达");
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    /** 判断网络是否连接 */
    private boolean isConnectIsNormal() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "MQTT当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "MQTT 没有可用网络");
            return false;
        }
    }

    @Override
    public void onDestroy() {
        stopSelf();
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}