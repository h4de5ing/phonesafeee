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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.ProcessBean;

/**
 * Created by Gh0st on 2015/9/9 21:57
 */


public class ProcessProvider {

    private static final String TAG = "ProcessProvider";

    /**
     * 获取进程的数据信息
     *
     * @param context
     * @return
     */
    public static List<ProcessBean> getProcessInfo(Context context) {
        List<ProcessBean> list = null;

        /*包管理器*/
        PackageManager pm = context.getPackageManager();

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);


        List<ActivityManager.RunningAppProcessInfo> running = am.getRunningAppProcesses();

        list = new ArrayList<ProcessBean>();
        for (ActivityManager.RunningAppProcessInfo info : running) {

            ProcessBean bean = new ProcessBean();
            //获取进程名称
            String packeName = info.processName;
            bean.packageName = packeName;
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = pm.getApplicationInfo(packeName, 0);

                bean.name = applicationInfo.loadLabel(pm).toString();
                bean.icon = applicationInfo.loadIcon(pm);

                int flags = applicationInfo.flags;

                //判断进程是否是系统进程
                if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    bean.isSystem = true;
                } else {
                    bean.isSystem = false;
                }

            } catch (PackageManager.NameNotFoundException e) {
                //默认找不到，因为手机系统中有些进程是使用C语言编写的
                bean.name = packeName;
                bean.icon = context.getResources().getDrawable(R.mipmap.ic_launcher);
                bean.isSystem = true;
            }

            //获取进程占用的内存
            Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[]{info.pid})[0];
            bean.memory = memoryInfo.getTotalPss() * 1024;
            list.add(bean);
        }

        return list;
    }

    /**
     * 获取所有进程数量
     *
     * @param context
     * @return
     */
    public static int getProcessTotal(Context context) {

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packList = pm.getInstalledPackages(0);

        Set<String> set = new HashSet<String>();
        for (PackageInfo info : packList) {

            ActivityInfo[] activities = info.activities;
            if (activities != null) {
                for (ActivityInfo ac : activities) {
                    set.add(ac.processName);
                }
            }

            ServiceInfo[] services = info.services;
            if (services != null) {
                for (ServiceInfo si : services) {
                    set.add(si.processName);
                }
            }

            ProviderInfo[] providers = info.providers;
            if (providers != null) {
                for (ProviderInfo pi : providers) {
                    set.add(pi.processName);
                }
            }

            ActivityInfo[] receivers = info.receivers;
            if (receivers != null) {
                for (ActivityInfo ai : receivers) {
                    set.add(ai.processName);
                }
            }

            set.add(info.applicationInfo.processName);
        }
        return set.size();
    }

    /**
     * 获取正在运行的进程数量
     *
     * @param context
     * @return
     */
    public static int getProcessRunningCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        /*获取进程数量*/
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = am.getRunningAppProcesses();

        return runningAppProcessInfoList.size();
    }

    /**
     * 获取手机内存的大小
     *
     * @param context
     * @return
     */
    public static long getMemoryCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);


        /*这里要进行版本的适配*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Log.i(TAG, "目前手机是AIP 16以上的版本 ");
            return memoryInfo.totalMem;
        } else {
            Log.i(TAG, "目前手机是AIP 16以下的版本");
            return getLowVersionMemoryTotal();
        }
    }

    private static long getLowVersionMemoryTotal() {
        //低版本的手机可以去系统中查询系统文件中的配置信息
        //手机系统是基于Linux系统中，
        // 所有系统中的硬件信息的配置信息都是写在硬盘中
        // proc/meminfo文件中

            /*MemTotal:         900792 kB*/

        long total = 0;
        try {
            File file = new File("proc/meminfo");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            line = line.replace("MemTotal:", "");
            line = line.replace("kB", "");
            line = line.trim();
            total = new Integer(line) * 1000;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }

    /**
     * 获取手机可用的内存
     *
     * @param context
     * @return
     */
    public static long getVaildMemomy(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }


    /**
     * 清理进程
     *
     * @param context
     * @param packageName
     */
    public static void cleanProcess(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(packageName);
    }
}
