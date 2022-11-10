package com.zorro.mediademo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, NetStateChangeObserver, IGetMessageCallBack {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btn_rotate_0, btn_rotate_90, btn_rotate_180, btn_rotate_270, btn_player, btn_clear, btn_show, btn_hide,
            btn_start_mqtt, btn_send, btn_dynamic, btn_restart;
    private TextView tv_storage, tv_ip, tv_model, tv_network_type;

    private FloatWindowService.FloatBinder floatBinder;

    private String clientId = "AndroidClient_";
    private MqttAndroidClient mqttAndroidClient;
    private String host = "tcp://210.73.216.2:1883";
    private MqttConnectOptions conOpt;
    private String device_id, store_id;

    //
    private MyServiceConnection myServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_rotate_0 = findViewById(R.id.btn_rotate_0);
        btn_rotate_90 = findViewById(R.id.btn_rotate_90);
        btn_rotate_180 = findViewById(R.id.btn_rotate_180);
        btn_rotate_270 = findViewById(R.id.btn_rotate_270);
        tv_storage = findViewById(R.id.tv_storage);
        btn_player = findViewById(R.id.btn_player);
        btn_clear = findViewById(R.id.btn_clear);
        btn_show = findViewById(R.id.btn_show);
        btn_hide = findViewById(R.id.btn_hide);
        tv_ip = findViewById(R.id.tv_ip);
        tv_model = findViewById(R.id.tv_model);
        tv_network_type = findViewById(R.id.tv_network_type);
        btn_send = findViewById(R.id.btn_send);
        btn_dynamic = findViewById(R.id.btn_dynamic);
        btn_restart = findViewById(R.id.btn_restart);
        btn_start_mqtt = findViewById(R.id.btn_start_mqtt);
        btn_send.setOnClickListener(this);
        btn_rotate_0.setOnClickListener(this);
        btn_rotate_90.setOnClickListener(this);
        btn_rotate_180.setOnClickListener(this);
        btn_rotate_270.setOnClickListener(this);
        btn_player.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_show.setOnClickListener(this);
        btn_hide.setOnClickListener(this);
        btn_dynamic.setOnClickListener(this);
        btn_restart.setOnClickListener(this);
        btn_start_mqtt.setOnClickListener(this);
        tv_network_type.setText("当前网络状态：" + getNetWorkType());
        tv_model.setText("手机型号：" + getSystemModel());
        tv_ip.setText("当前IP：" + getIPAddress());
        tv_storage.setText("可用：" + getSDAvailableSize() + "/总量：" + getSDTotalSize());

        device_id = SharePreferenceUtils.getString(this, "device_id");
        store_id = SharePreferenceUtils.getString(this, "store_id");

        myServiceConnection = new MyServiceConnection();
        myServiceConnection.setIGetMessageCallBack(this);

        Log.d(TAG,DeviceIdUtil.getDeviceId(this));

        getStoragePermission();
        checkFloatPermission();
        //链接MQTT
        //setMqtt();
        NetStateChangeReceiver.registerObserver(this);
        NetStateChangeReceiver.registerReceiver(this);
    }

    private void setMqtt() {
        clientId = clientId + device_id;
        mqttAndroidClient = new MqttAndroidClient(this, host, clientId);
        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(20);
        conOpt.setUserName("1");
        conOpt.setPassword("123".toCharArray());
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "connectionLost: 连接断开");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, "消息到达");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        try {
            //进行连接
            mqttAndroidClient.connect(conOpt, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "onSuccess: 连接成功");
                    try {
                        //连接成功后订阅主题
                        mqttAndroidClient.subscribe(store_id, 2);

                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "onFailure: 连接失败");
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String payload) {
        try {
            if (mqttAndroidClient.isConnected() == false) {
                mqttAndroidClient.connect();
            }

            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            message.setQos(0);
            mqttAndroidClient.publish(store_id, message, null, new IMqttActionListener() {
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

    private void getStoragePermission() {
        if (!XXPermissions.isGranted(MainActivity.this, Permission.Group.STORAGE)) {
            XXPermissions.with(this)
                    .permission(Permission.Group.STORAGE)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                Toast.makeText(MainActivity.this, "已获得存储权限", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onDenied(List<String> permissions, boolean never) {
                            Toast.makeText(MainActivity.this, "没获取到存储权限，无法实现边下边播", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void checkFloatPermission() {
        if (!XXPermissions.isGranted(MainActivity.this, Permission.SYSTEM_ALERT_WINDOW)) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    //标题
                    .setTitle("权限申请")
                    //内容
                    .setMessage("请在打开的窗口的权限中开启悬浮窗权限，以正常使用本应用")
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            exit();
                        }
                    })
                    .setNeutralButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getFloatPermission();
                        }
                    })
                    .create();
            alertDialog.show();
        } else {

        }
    }

    private void getFloatPermission() {
        XXPermissions.with(MainActivity.this)
                .permission(Permission.SYSTEM_ALERT_WINDOW)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            Toast.makeText(MainActivity.this, "已获得悬浮窗权限", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        Toast.makeText(MainActivity.this, "设置悬浮窗权限失败，请重新设置", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void exit() {
        try {
            // 退出JVM,释放所占内存资源,0表示正常退出
            System.exit(0);
            // 从系统中kill掉应用程序
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_rotate_0:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case R.id.btn_rotate_90:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case R.id.btn_rotate_180:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case R.id.btn_rotate_270:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case R.id.btn_player:
                startActivity(new Intent(this, SimplePlayerActivity.class));
                break;
            case R.id.btn_clear:
                //清理缓存
                GSYVideoManager.instance().clearAllDefaultCache(MainActivity.this);
                break;
            case R.id.btn_show:
                Intent bindIntent = new Intent(this, FloatWindowService.class);
                bindService(bindIntent, connection, BIND_AUTO_CREATE);
                break;
            case R.id.btn_hide:
                unbindService(connection);
                break;
            case R.id.btn_send:
                MqttService.publishMessage("Hello!");
                break;
            case R.id.btn_dynamic:
                startActivity(new Intent(this, DynamicLayoutActivity.class));
                break;
            case R.id.btn_restart:
                restart();
                break;
            case R.id.btn_start_mqtt:
                startMqttService();
                break;
        }
    }

    private void startMqttService() {
        Intent intent = new Intent(this, MqttService.class);
        bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void restart() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        ((AlarmManager) getSystemService(ALARM_SERVICE)).set(AlarmManager.RTC, System.currentTimeMillis() + 1500L, pendingIntent);
        System.exit(0);
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            floatBinder = (FloatWindowService.FloatBinder) iBinder;
            FloatWindowService floatWindowService = floatBinder.getService();
            floatWindowService.setCallback(new FloatWindowService.Callback() {
                @Override
                public void closeClick() {
                    Log.d(TAG, "closeClick");
                }

                @Override
                public void nextClick() {
                    Log.d(TAG, "nextClick");
                    startActivity(new Intent(MainActivity.this, SimplePlayerActivity.class));
                }

                @Override
                public void playClick() {
                    Log.d(TAG, "playClick");
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /**
     * 获得SD卡总大小
     *
     * @return
     */
    private String getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return Formatter.formatFileSize(MainActivity.this, blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    private String getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return Formatter.formatFileSize(MainActivity.this, blockSize * availableBlocks);
    }


    /**
     * 获取网络状态
     */
    private String getNetWorkType() {
        Context context = MainActivity.this;
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G/5G网络
                return "移动网络";
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                return "Wifi";
            }
        } else {
            return "当前无网络连接";
        }
        return null;
    }

    /**
     * 获得IP地址，分为两种情况，一是wifi下，二是移动网络下，得到的ip地址是不一样的
     */
    private String getIPAddress() {
        Context context = MainActivity.this;
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //调用方法将int转换为地址字符串
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return "xx.xx.xx.xx";
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    private String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetStateChangeReceiver.unRegisterObserver(this);
        NetStateChangeReceiver.unRegisterReceiver(this);
    }

    @Override
    public void onNetDisconnected() {
        tv_network_type.setText("无网络连接");
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
        tv_network_type.setText(networkType.toString());
    }

    @Override
    public void setMessage(String message) {
        Log.d(TAG, "获取的消息" + message);
    }
}