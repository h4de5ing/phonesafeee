package mo.com.phonesafe.preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Gh0st on 2015/8/30 13:11
 * <p/>
 * 获取一个配置和用户信息的工具类
 */


public class PreferenceUtils {
    private static SharedPreferences mPreferences;

    private static SharedPreferences getSp(Context context) {
        if (mPreferences == null) {
            mPreferences = context.getSharedPreferences("safe", Context.MODE_PRIVATE);
        }
        return mPreferences;
    }

    /**
     * 设置shared_prefrence的
     *
     * @param context 上下文
     * @param key     键
     * @param value   默认值
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = getSp(context);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    /**
     * 获得boolean类型的数据
     *
     * @param context 上下文
     * @param key     键
     * @param deValue 默认值
     */
    public static boolean getBoolean(Context context, String key, boolean deValue) {
        SharedPreferences sp = getSp(context);
        return sp.getBoolean(key, deValue);
    }

    /**
     * 获得boolean类型的数据,没有时默认值为false
     *
     * @param context 上下文
     * @param key     键
     * @return 返回值
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);

    }

    /**
     * 获得String类型的数据,没有时默认值为null
     *
     * @param context 上下文
     * @param key     键
     * @return 返回值
     */
    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    /**
     * 获得String类型的数据,没有时默认值为null
     *
     * @param context 上下文
     * @param key     键
     * @return 返回值
     */
    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = getSp(context);
        return sp.getString(key, defValue);
    }


    /**
     * 保存String类型的数据,没有时默认值为null
     *
     * @param context 上下文
     * @param key     键
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = getSp(context);
        sp.edit().putString(key, value).commit();
    }

    /**
     * 获得int类型的数据,没有时默认值为-1
     *
     * @param context 上下文
     * @param key     键
     * @return 返回值
     */
    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    /**
     * 获得int类型的数据,没有时默认值为null
     *
     * @param context 上下文
     * @param key     键
     * @return 返回值
     */
    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sp = getSp(context);
        return sp.getInt(key, defValue);
    }


    /**
     * 保存int类型的数据,没有时默认值为-1
     *
     * @param context 上下文
     * @param key     键
     */
    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = getSp(context);
        sp.edit().putInt(key, value).commit();
    }

}
