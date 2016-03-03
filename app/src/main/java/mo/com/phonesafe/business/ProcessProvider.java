package mo.com.phonesafe.business;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mo.com.phonesafe.bean.ProcessBean;

/**
 * Created by Gh0st on 2016/3/3 003.
 */
public class ProcessProvider {
    private static final String TAG = "ProcessProvider";

    /**
     * @param context     上下文
     * @param packageName 包名称
     */
    public static void cleanProcess(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(packageName);
    }

    /**
     * @param context 上下文
     * @return 总内存
     */
    public static long getMemoryCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return outInfo.totalMem;
        } else {
            return loadLowVersionTotalMemory();
        }
    }

    /**
     * @param context 上下文
     * @return 可用内存
     */
    public static long getVaildMemomy(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    /**
     * @return 返回正在运行的进程数量
     */
    public static int getProcessRunningCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        Log.i(TAG, "getRuningProcessCount 运行进程数量" + runningAppProcesses.size());
        return runningAppProcesses.size();
    }

    /**
     * @param context 上下文
     * @return 所有进程的数量
     */
    public static int getProcessTotal(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        Set<String> set = new HashSet<String>(); //利用set去重

        for (PackageInfo pkg : packages) {

            ActivityInfo[] activities = pkg.activities;
            if (activities != null) {
                for (ActivityInfo activityInfo : activities) {
                    set.add(activityInfo.processName);
                }
            }

            ServiceInfo[] services = pkg.services;
            if (services != null) {
                for (ServiceInfo serviceInfo : services) {
                    set.add(serviceInfo.processName);
                }
            }

            ProviderInfo[] providers = pkg.providers;
            if (providers != null) {
                for (ProviderInfo providerInfo : providers) {
                    set.add(providerInfo.packageName);
                }
            }

            ActivityInfo[] receivers = pkg.receivers;
            if (receivers != null) {
                for (ActivityInfo receiver : receivers) {
                    set.add((receiver.processName));
                }
            }
            set.add(pkg.applicationInfo.processName);
        }
        return set.size();
    }

    /**
     * @param context 上下文
     * @return 获取正在运行的进程信息
     */
    public static List<ProcessBean> getProcessInfo(Context context) {
        PackageManager pm = context.getPackageManager();

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ProcessBean> list = new ArrayList<ProcessBean>();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        ApplicationInfo applicationInfo = null;
        for (ActivityManager.RunningAppProcessInfo runapp : runningAppProcesses) {
            ProcessBean bean = new ProcessBean();
            try {
                applicationInfo = pm.getApplicationInfo(runapp.processName, 0);
                bean.name = applicationInfo.loadLabel(pm).toString();
                if (applicationInfo.loadIcon(pm) != null) {
                    bean.icon = applicationInfo.loadIcon(pm);
                } else {
                    Log.i(TAG, "系统的进程没有图标");
                }
                bean.packageName = runapp.processName;

                //判断是否是系统进程
                int flags = applicationInfo.flags;
                if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    bean.isSystem = true;
                } else {
                    bean.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                bean.name = runapp.processName;
                bean.isSystem = true;
            }
            Debug.MemoryInfo memory = am.getProcessMemoryInfo(new int[]{runapp.pid})[0];
            bean.memory = memory.getTotalPss() * 1024;
            list.add(bean);
        }
        return list;
    }

    //获取低版本系统的内存信息
    public static long loadLowVersionTotalMemory() {
        String s = null;
        File file = new File("/proc/meminfo");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = reader.readLine();
            s = line.replace("MemTotal:", "").toString().replace("kB", "").trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Long.valueOf(s) * 1024;
    }
}
