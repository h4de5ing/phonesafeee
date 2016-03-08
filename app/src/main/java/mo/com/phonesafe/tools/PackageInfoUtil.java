package mo.com.phonesafe.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Gh0st on 2015/8/29 12:24
 * <p/>
 * 获取版本信息工具类
 */


public class PackageInfoUtil {

    /**
     * 获取版本名
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) throws PackageManager.NameNotFoundException {
        //版本名显示 版本号 检测判断是否有新的版本
        PackageManager pm = context.getPackageManager();

        String versionName;
        //获取当前app包的信息
        PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);

        //版本名称
        versionName = packageInfo.versionName;

        return versionName;
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public static int getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        //版本名显示 版本号 检测判断是否有新的版本
        PackageManager pm = context.getPackageManager();

        int versionCode = 0;
        //获取当前app包的信息
        PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);

        //版本名称
        versionCode = packageInfo.versionCode;

        return versionCode;

    }

}
