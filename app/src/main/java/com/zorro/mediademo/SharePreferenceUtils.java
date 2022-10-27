package com.zorro.mediademo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * @Author : cbx
 * @Email : 673591077@qq.com
 * @Date : on 2022-10-13 09:41.
 * @Description :SharePreference工具类
 */
public class SharePreferenceUtils {

    private static String mName = "SharePreference_Data";
    private static SharedPreferences.Editor mEditor;

    /**
     * 设置SharePreferenceName 默认是SharePreference_Data
     *
     * @param name
     */
    public static void setSharePreferenceName(String name) {
        mName = name;
    }

    /**
     * put string val
     *
     * @param context
     * @param key
     * @param val
     */
    public static void putString(Context context, String key, String val) {
        put(context, key, val);
    }

    /**
     * get string val
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        return (String) get(context, key, "");
    }

    /**
     * get string val
     *
     * @param context
     * @param key
     * @param defaultVal
     * @return
     */
    public static String getString(Context context, String key, String defaultVal) {
        return (String) get(context, key, defaultVal);
    }

    /**
     * put int val
     *
     * @param context
     * @param key
     * @param val
     */
    public static void putInt(Context context, String key, int val) {
        put(context, key, val);
    }

    /**
     * get int val
     *
     * @param context
     * @param key
     * @return
     */
    public static int getInt(Context context, String key) {
        return (int) get(context, key, 0);
    }

    /**
     * get int val
     *
     * @param context
     * @param key
     * @param defaultVal
     * @return
     */
    public static int getInt(Context context, String key, int defaultVal) {
        return (int) get(context, key, defaultVal);
    }

    /**
     * put bool val
     *
     * @param context
     * @param key
     * @param val
     */
    public static void putBool(Context context, String key, Boolean val) {
        put(context, key, val);
    }

    /**
     * get bool val
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getBool(Context context, String key) {
        return (boolean) get(context, key, false);
    }

    /**
     * get bool val
     *
     * @param context
     * @param key
     * @param defaultVal
     * @return
     */
    public static boolean getBool(Context context, String key, Boolean defaultVal) {
        return (boolean) get(context, key, defaultVal);
    }

    /**
     * put float val
     *
     * @param context
     * @param key
     * @param val
     */
    public static void putFloat(Context context, String key, Float val) {
        put(context, key, val);
    }

    /**
     * get float val
     *
     * @param context
     * @param key
     * @return
     */
    public static float getFloat(Context context, String key) {
        return (float) get(context, key, 0.0f);
    }

    /**
     * get float val
     *
     * @param context
     * @param key
     * @param defaultVal
     * @return
     */
    public static float getFloat(Context context, String key, Float defaultVal) {
        return (float) get(context, key, defaultVal);
    }

    /**
     * put long int val
     *
     * @param context
     * @param key
     * @param val
     */
    public static void putLong(Context context, String key, Long val) {
        put(context, key, val);
    }

    /**
     * get long val
     *
     * @param context
     * @param key
     * @return
     */
    public static long getLong(Context context, String key) {
        return (long) get(context, key, 0L);
    }

    /**
     * get long val
     *
     * @param context
     * @param key
     * @param defaultVal
     * @return
     */
    public static long getLong(Context context, String key, Long defaultVal) {
        return (long) get(context, key, defaultVal);
    }

    /**
     * 保存图片到SharedPreferences
     *
     * @param mContext
     * @param imageView
     */
    public static void putImage(Context mContext, String key, ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        // 将Bitmap压缩成字节数组输出流
        ByteArrayOutputStream byStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byStream);
        // 利用Base64将我们的字节数组输出流转换成String
        byte[] byteArray = byStream.toByteArray();
        String imgString = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
        // 将String保存shareUtils
        put(mContext, key, imgString);
    }

    /**
     * 从SharedPreferences读取图片
     *
     * @param mContext
     * @param imageView
     */
    public static Bitmap getImage(Context mContext, String key, ImageView imageView) {
        String imgString = (String) get(mContext, key, "");
        if (!imgString.equals("")) {
            // 利用Base64将我们string转换
            byte[] byteArray = Base64.decode(imgString, Base64.DEFAULT);
            ByteArrayInputStream byStream = new ByteArrayInputStream(byteArray);
            // 生成bitmap
            return BitmapFactory.decodeStream(byStream);
        }
        return null;
    }

    /**
     * 返回所有的键值对
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(mName,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public static void remove(Context context, String key) {
        if (mEditor == null) {
            setEditor(context);
        }
        mEditor.remove(key);
        mEditor.commit();
    }

    /**
     * 清除所有的数据
     */
    public static void clear(Context context) {
        if (mEditor == null) {
            setEditor(context);
        }
        mEditor.clear();
        mEditor.commit();
    }

    /**
     * 查询某个key是否存在
     *
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(mName, Context.MODE_PRIVATE);
        return sharedPreferences.contains(key);
    }

    private static void setEditor(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(mName, Context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
    }

    private static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(mName, Context.MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return sharedPreferences.getString(key, null);
        }
    }

    private static void put(Context context, String key, Object object) {
        if (mEditor == null) {
            setEditor(context);
        }

        if (object instanceof String) {
            mEditor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            mEditor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            mEditor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            mEditor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            mEditor.putLong(key, (Long) object);
        } else {
            mEditor.putString(key, object.toString());
        }
        mEditor.commit();
    }

}
