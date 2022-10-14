package com.zorro.mediademo;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import javax.security.auth.callback.Callback;

/**
 * @Author : cbx
 * @Email : 673591077@qq.com
 * @Date : on 2022-10-13 10:53.
 * @Description :描述
 */
public class FloatWindowService extends Service {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private DisplayMetrics mDisplayMetrics;
    private View mFloatingLayout;
    private boolean isMove = false;
    private LinearLayout layout_float;

    private ImageView fm_float_play, fm_float_next, fm_float_close;

    /**
     * 回调
     */
    private Callback callback;

    private FloatBinder floatBinder = new FloatBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        initView();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return floatBinder;
    }

    /**
     * 提供接口回调方法
     * @param callback
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * 回调接口
     *
     */
    public static interface Callback {
        /**
         * 得到实时更新的数据
         *
         * @return
         */
        void closeClick();

        void nextClick();

        void playClick();
    }

    /**
     * 内部类继承Binder
     *
     * @author lenovo
     */
    public class FloatBinder extends Binder {
        /**
         * 声明方法返回值是MyService本身
         *
         * @return
         */
        public FloatWindowService getService() {
            return FloatWindowService.this;
        }
    }

    private void initView() {
        mWindowManager = (WindowManager) getBaseContext().getSystemService(WINDOW_SERVICE);

        mDisplayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);

        mLayoutParams = new WindowManager.LayoutParams();
        int layout_type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layout_type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layout_type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.type = layout_type;
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mFloatingLayout = LayoutInflater.from(this).inflate(R.layout.float_view, null);
        layout_float = mFloatingLayout.findViewById(R.id.layout_float);
        layout_float.setOnTouchListener(new WindowTouchListener());
        fm_float_play = mFloatingLayout.findViewById(R.id.fm_float_play);
        fm_float_next = mFloatingLayout.findViewById(R.id.fm_float_next);
        fm_float_close = mFloatingLayout.findViewById(R.id.fm_float_close);
        fm_float_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.playClick();
                }
            }
        });
        fm_float_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.nextClick();
                }
            }
        });
        fm_float_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
                if (callback != null) {
                    callback.closeClick();
                }
            }
        });
        mWindowManager.addView(mFloatingLayout, mLayoutParams);
    }

    //开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
    private int mTouchStartX;
    private int mTouchStartY;
    private int mTouchCurrentX;
    private int mTouchCurrentY;
    //开始时的坐标和结束时的坐标（相对于自身控件的坐标）
    private int mStartX;
    private int mStartY;
    private int mStopX;
    private int mStopY;

    class WindowTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    mTouchStartX = (int) motionEvent.getRawX();
                    mTouchStartY = (int) motionEvent.getRawY();
                    mStartX = (int) motionEvent.getX();
                    mStartY = (int) motionEvent.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mTouchCurrentX = (int) motionEvent.getRawX();
                    mTouchCurrentY = (int) motionEvent.getRawY();
                    mLayoutParams.x += mTouchCurrentX - mTouchStartX;
                    mLayoutParams.y += mTouchCurrentY - mTouchStartY;
                    mWindowManager.updateViewLayout(mFloatingLayout, mLayoutParams);
                    mTouchStartX = mTouchCurrentX;
                    mTouchStartY = mTouchCurrentY;
                    break;
                case MotionEvent.ACTION_UP:
                    mStopX = (int) motionEvent.getX();
                    mStopY = (int) motionEvent.getY();
                    if (Math.abs(mStartX - mStopX) >= 1 || Math.abs(mStartY - mStopY) >= 1) {
                        isMove = true;
                    }
                    break;
            }
            return false;
        }
    }

    private void show() {
        if (mFloatingLayout != null) {
            mFloatingLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hide() {
        if (mFloatingLayout != null) {
            mFloatingLayout.setVisibility(View.GONE);
        }
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
    public void onDestroy() {
        super.onDestroy();
        mWindowManager.removeView(mFloatingLayout);
    }
}
