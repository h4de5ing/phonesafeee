package mo.com.phonesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import mo.com.phonesafe.bean.ProcessBean;
import mo.com.phonesafe.business.ProcessProvider;

/**
 * 作者：MoMxMo on 2015/9/11 00:16
 * 邮箱：xxxx@qq.com
 *
 * 锁屏自动清理服务
 */


public class AutoCleanService extends Service {
    private static final String TAG = "AutoCleanService";
    private LockScreenReceiver lsr;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate 开启锁屏自动清理服务");


        lsr = new LockScreenReceiver();
        //使用广播监听手机锁屏的状态
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);//设置优先级
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lsr, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onCreate 关闭锁屏自动清理服务");

        //注销广播
        if (lsr!=null) {
            unregisterReceiver(lsr);
            lsr = null;
        }
    }

    /**
     * 手机锁屏广播接收者
     */
    private class LockScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "手机锁屏了");

            //清理手机进程
            /*获取所有后台进程，并杀死所有进程*/
            List<ProcessBean> list = ProcessProvider.getProcessInfo(AutoCleanService.this);
            for (ProcessBean bean : list) {
                if (bean.packageName.equals(getPackageName())) {
                    ProcessProvider.cleanProcess(AutoCleanService.this, bean.packageName);
                }
            }
        }
    }
}
