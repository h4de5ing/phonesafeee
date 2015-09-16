package mo.com.phonesafe.business;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mo.com.phonesafe.bean.AppBean;

/**
 * 手机程序应用的所有信息
 *
 * 作者：MoMxMo on 2015/9/8 14:10
 * 邮箱：xxxx@qq.com
 */


public class AppProvider {



    /**
     * 获取所有有启动的图标的APP信息
     * @param context
     * @return
     */
    public static List<AppBean> getAllLauncherApps(Context context) {
        //获取App数据
        PackageManager pm = context.getPackageManager();
        //获取包列表
        List<PackageInfo> listApp = pm.getInstalledPackages(0);

        AppBean bean = null;
        List<AppBean> listdata = null;
        listdata = new ArrayList<AppBean>();

        for (PackageInfo packageinfo : listApp) {
            bean = new AppBean();
            //获取包名
            bean.packageName = packageinfo.packageName;
            Intent intent = pm.getLaunchIntentForPackage(packageinfo.packageName);

            //判断是否有启动的图标
            if (intent == null) {
                continue;
            }
            //获取应用程序的信息对象
            ApplicationInfo applicationInfo = packageinfo.applicationInfo;

            /*获取应用名称*/
            String appName = applicationInfo.loadLabel(pm).toString();
            bean.name = appName;

            /*获取应用图标*/
            Drawable appIcon = applicationInfo.loadIcon(pm);
            bean.icon = appIcon;

            /*获取应用大小*/
            String sourceDir = applicationInfo.sourceDir;
            File sorurcFile = new File(sourceDir);
            bean.size = sorurcFile.length();

            /*应用的功能标记*/
            int flags = applicationInfo.flags;

            /*判断应用是否是系统内部的程序（通过功能的匹配）*/
            if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                bean.isSystem = true;
            } else {
                bean.isSystem = false;
            }

            /*判断应用是否安装在SD卡中*/
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                bean.isInstallSD = true;
            } else {
                bean.isInstallSD = false;
            }

            listdata.add(bean);
        }
        return listdata;
    }

    /**
     * 获取所有应用的信息
     * @param context
     * @return
     */
    public static List<AppBean> getAppManagerInfo(Context context) {
        //获取App数据
        PackageManager pm = context.getPackageManager();
        //获取包列表
        List<PackageInfo> listApp = pm.getInstalledPackages(0);

        AppBean bean = null;
        List<AppBean> listdata = null;
        listdata = new ArrayList<AppBean>();

        for (PackageInfo packageinfo : listApp) {
            bean = new AppBean();
            //获取包名
            bean.packageName = packageinfo.packageName;

            //获取应用程序的信息对象
            ApplicationInfo applicationInfo = packageinfo.applicationInfo;

            /*获取应用名称*/
            String appName = applicationInfo.loadLabel(pm).toString();
            bean.name = appName;

            /*获取应用图标*/
            Drawable appIcon = applicationInfo.loadIcon(pm);
            bean.icon = appIcon;

            /*获取应用大小*/
            String sourceDir = applicationInfo.sourceDir;
            File sorurcFile = new File(sourceDir);
            bean.size = sorurcFile.length();

            /*应用的功能标记*/
            int flags = applicationInfo.flags;

            /*判断应用是否是系统内部的程序（通过功能的匹配）*/
            if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                bean.isSystem = true;
            } else {
                bean.isSystem = false;
            }

            /*判断应用是否安装在SD卡中*/
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                bean.isInstallSD = true;
            } else {
                bean.isInstallSD = false;
            }

            listdata.add(bean);
        }
        return listdata;
    }
}
